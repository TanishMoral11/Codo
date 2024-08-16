package com.example.codeforcesanalyser

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.codeforcesanalyser.databinding.ActivityMainBinding
import com.example.codeforcesanalyzer.UserInfoResponse
import com.example.codeforcesanalyzer.responseDataClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Find the TextView by its ID
        val developerInfo: TextView = findViewById(R.id.tvDeveloperInfo)

        // Set the HTML formatted text
        developerInfo.text = Html.fromHtml("Developed with ❤️ by <b>Tanish Moral</b>")


        binding.btnFind.setOnClickListener {
            val handle = binding.etHandle.text.toString()
            if (handle.isNotEmpty()) {
                getUserInfo(handle)
            } else {
                Toast.makeText(this, "Please enter a Codeforces handle", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvDeveloperInfo.text = Html.fromHtml("Developed with ❤️ by <b>Tanish Moral</b>", Html.FROM_HTML_MODE_LEGACY)
        binding.tvDeveloperInfo.setOnClickListener {
            openLinkedInProfile()
        }
    }

    private fun openLinkedInProfile() {
        val linkedInUrl = "https://www.linkedin.com/in/tanishmoral/"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkedInUrl))
        startActivity(intent)
    }

    private fun getUserInfo(handle: String) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait...")
        progressDialog.show()

        RetrofitInstance.apiInterface.getUserInfo(handle).enqueue(object : Callback<UserInfoResponse?> {
            override fun onResponse(
                call: Call<UserInfoResponse?>,
                response: Response<UserInfoResponse?>
            ) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    val userInfo = response.body()?.result?.firstOrNull()
                    if (userInfo != null) {
                        // Set User Info
                        binding.tvHandle.text = "Handle: ${userInfo.handle}"
                        binding.tvEmail.text = "Email: ${userInfo.email ?: "N/A"}"
                        binding.tvRank.text = "Rank: ${userInfo.rank}"
                        binding.tvRating.text = "Rating: ${userInfo.rating}"
                        binding.tvMaxRank.text = "Max Rank: ${userInfo.maxRank}"
                        binding.tvMaxRating.text = "Max Rating: ${userInfo.maxRating}"
                        binding.tvFriendCount.text = "Friends: ${userInfo.friendOfCount}"

                        // Convert Unix Time to Date
                        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        binding.tvLastOnline.text = "Last Online: ${sdf.format(Date(userInfo.lastOnlineTimeSeconds * 1000))}"
                        binding.tvRegistrationTime.text = "Registered On: ${sdf.format(Date(userInfo.registrationTimeSeconds * 1000))}"

                        // Load Avatar
                        Glide.with(this@MainActivity)
                            .load(userInfo.avatar)
                            .into(binding.ivAvatar)

                        // Fetch and set the user's rating history to get the last contest information
                        getData(handle)
                    } else {
                        Toast.makeText(this@MainActivity, "User not found", Toast.LENGTH_LONG).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@MainActivity, "Error: ${response.code()} - ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<UserInfoResponse?>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getData(handle: String) {
        RetrofitInstance.apiInterface.getRatingData(handle).enqueue(object : Callback<responseDataClass?> {
            override fun onResponse(
                call: Call<responseDataClass?>,
                response: Response<responseDataClass?>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()?.result
                    if (!result.isNullOrEmpty()) {
                        val latestRating = result.last().newRating
                        val rank = result.last().rank
                        val lastContest = result.last().contestName
                        val ratingChange = result.last().newRating - result.last().oldRating

                        binding.tvLastContest.text = "Last Contest: $lastContest"
                        binding.tvRatingChange.text = "Rating Change: $ratingChange"
                    } else {
                        binding.tvLastContest.text = "No contest data available"
                        binding.tvRatingChange.text = "N/A"
                    }
                }
            }

            override fun onFailure(call: Call<responseDataClass?>, t: Throwable) {
                // Handle error
            }
        })
    }
}

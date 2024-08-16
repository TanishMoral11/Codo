package com.example.codeforcesanalyser

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.Html
import android.text.style.StyleSpan
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
        developerInfo.text = Html.fromHtml("Developed with ❤️ by <b>Tanish Moral</b>", Html.FROM_HTML_MODE_LEGACY)

        binding.btnFind.setOnClickListener {
            val handle = binding.etHandle.text.toString()
            if (handle.isNotEmpty()) {
                getUserInfo(handle)
            } else {
                Toast.makeText(this, "Please enter a Codeforces handle", Toast.LENGTH_SHORT).show()
            }
        }

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
                        // Set User Info with formatted text
                        setTextWithBoldLabel(binding.tvHandle, "Handle:", userInfo.handle)
                        setTextWithBoldLabel(binding.tvEmail, "Email:", userInfo.email ?: "N/A")
                        setTextWithBoldLabel(binding.tvRank, "Rank:", userInfo.rank)
                        setTextWithBoldLabel(binding.tvRating, "Rating:",
                            userInfo.rating.toString()
                        )
                        setTextWithBoldLabel(binding.tvMaxRank, "Max Rank:", userInfo.maxRank)
                        setTextWithBoldLabel(binding.tvMaxRating, "Max Rating:",
                            userInfo.maxRating.toString()
                        )
                        setTextWithBoldLabel(binding.tvFriendCount, "Friends:", userInfo.friendOfCount.toString())

                        // Convert Unix Time to Date
                        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        setTextWithBoldLabel(binding.tvLastOnline, "Last Online:", sdf.format(Date(userInfo.lastOnlineTimeSeconds * 1000)))
                        setTextWithBoldLabel(binding.tvRegistrationTime, "Registered On:", sdf.format(Date(userInfo.registrationTimeSeconds * 1000)))

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

                        setTextWithBoldLabel(binding.tvLastContest, "Last Contest:", lastContest)
                        setTextWithBoldLabel(binding.tvRatingChange, "Rating Change:", ratingChange.toString())
                    }
                    else {
                        setTextWithBoldLabel(binding.tvLastContest, "Last Contest:", "No contest data available")
                        setTextWithBoldLabel(binding.tvRatingChange, "Rating Change:", "N/A")
                    }
                }
            }

            override fun onFailure(call: Call<responseDataClass?>, t: Throwable) {
                // Handle error
            }
        })
    }

    private fun setTextWithBoldLabel(textView: TextView, label: String, value: String) {
        val spannable = SpannableString("$label $value")
        spannable.setSpan(StyleSpan(android.graphics.Typeface.BOLD), 0, label.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannable
    }
}

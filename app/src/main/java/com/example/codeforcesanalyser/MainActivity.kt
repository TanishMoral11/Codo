package com.example.codeforcesanalyser

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.codeforcesanalyser.databinding.ActivityMainBinding
import responseDataClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        enableEdgeToEdge()
        binding.btnFind.setOnClickListener {
            val handle = binding.etHandle.text.toString()
            if (handle.isNotEmpty()) {
                getData(handle)
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
    private fun getData(handle: String) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait...")
        progressDialog.show()

        Log.d(TAG, "Fetching data for handle: $handle")

        RetrofitInstance.apiInterface.getData(handle).enqueue(object : Callback<responseDataClass?> {
            override fun onResponse(
                call: Call<responseDataClass?>,
                response: Response<responseDataClass?>
            ) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    Log.d(TAG, "API call successful")
                    val result = response.body()?.result
                    if (!result.isNullOrEmpty()) {
                        val latestRating = result.last().newRating
                        val rank  = result.last().rank
                        Log.d(TAG, "Latest rating: $latestRating")
                        binding.tvHandle.text = "Handle : ${result.last().handle}"
                        binding.tvRating.text = "Current Rating: $latestRating"
                        binding.tvRank.text = "Rank  : $rank"
                        binding.tvLastContest.text = "Last Contest : ${result.last().contestName}"
                        binding.tvRatingChange.text  = "Rating Change : ${result.last().newRating - result.last().oldRating}"
                    } else {
                        Log.d(TAG, "No rating data available")
                        binding.tvRating.text = "No rating data available"
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "API call failed: ${response.code()}, Error body: $errorBody")
                    Toast.makeText(this@MainActivity, "Error: ${response.code()} - ${response.message()}", Toast.LENGTH_LONG).show()
                }

            }

            override fun onFailure(call: Call<responseDataClass?>, t: Throwable) {
                progressDialog.dismiss()
                Log.e(TAG, "API call failed", t)
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                t.printStackTrace()
            }
        })
    }
}
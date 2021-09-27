package uz.creator.worldnews.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import com.squareup.picasso.Picasso
import uz.creator.worldnews.R
import uz.creator.worldnews.databinding.ActivityMainBinding
import uz.creator.worldnews.repository.NewsRepository
import uz.creator.worldnews.utils.UtilMethods
import uz.creator.worldnews.viewmodel.NewsViewModel
import uz.creator.worldnews.viewmodel.NewsViewModelProviderFactory

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newsRepository = NewsRepository()
        val viewModelProviderFactory = NewsViewModelProviderFactory(application, newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

//        if (UtilMethods.isInternetAvailable(this)) {
//            hideNoInternetDialog()
//        } else {
//            showNoInternetDialog()
//        }

        binding.buttonTryAgain.setOnClickListener {

        }

        Picasso.get().load(R.drawable.no_internet_gif_2)
            .into(binding.noInternetImage)
    }

//    fun hideNoInternetDialog() {
//        binding.noInternetDialog.visibility = View.GONE
//    }
//
//    fun showNoInternetDialog() {
//        binding.noInternetDialog.visibility = View.VISIBLE
//    }
}

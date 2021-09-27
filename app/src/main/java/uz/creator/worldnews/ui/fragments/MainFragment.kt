package uz.creator.worldnews.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationView
import com.infideap.drawerbehavior.AdvanceDrawerLayout
import uz.creator.worldnews.R
import uz.creator.worldnews.adapters.MainViewpagerAdapter
import uz.creator.worldnews.databinding.FragmentMainBinding
import uz.creator.worldnews.utils.UtilMethods
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var drawerLayout: AdvanceDrawerLayout
    private lateinit var mainViewpagerAdapter: MainViewpagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        mainViewpagerAdapter = MainViewpagerAdapter(childFragmentManager)
        binding.appBar.mainViewPager.adapter = mainViewpagerAdapter
        binding.appBar.bottomBar.onItemSelected = { position ->
            when (position) {
                0 -> {
                    binding.appBar.mainViewPager.currentItem = 0
                }
                1 -> {
                    binding.appBar.mainViewPager.currentItem = 1
                }
                else -> {
                    binding.appBar.mainViewPager.currentItem = 2
                }
            }
        }
        binding.appBar.mainViewPager.addOnPageChangeListener(object :
            ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        binding.appBar.bottomBar.itemActiveIndex = 0
                    }
                    1 -> {
                        binding.appBar.bottomBar.itemActiveIndex = 1
                    }
                    else -> {
                        binding.appBar.bottomBar.itemActiveIndex = 2
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION)
        drawerLayout = binding.drawerLayout
        val toggle = ActionBarDrawerToggle(
            requireActivity(),
            binding.drawerLayout,
            binding.appBar.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        })
        binding.drawerLayout.setViewScale(Gravity.START, 0.8f)
        binding.drawerLayout.setRadius(Gravity.START, 35f)
        binding.drawerLayout.setViewElevation(Gravity.START, 80f)
        getCurrentTime()
        return binding.root
    }


    private fun getCurrentTime() {
        val dateFormatter = SimpleDateFormat("HH")
        dateFormatter.isLenient = false
        val today = Date()
        val s = dateFormatter.format(today).toInt()

        binding.appBar.tvMain.text = when {
            s < 5 -> "Welcome!"
            s in 5..11 -> "Good Morning!"
            s in 12..13 -> "Good Afternoon!"
            else -> "Good Evening!"
        }
    }
}
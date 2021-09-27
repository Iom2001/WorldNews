package uz.creator.worldnews.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import uz.creator.worldnews.ui.fragments.ExploreFragment
import uz.creator.worldnews.ui.fragments.HomeFragment
import uz.creator.worldnews.ui.fragments.SavedFragment

class MainViewpagerAdapter(
    fragmentManager: FragmentManager
) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int = 3

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                HomeFragment()
            }
            1 -> {
                ExploreFragment()
            }
            else -> {
                SavedFragment()
            }
        }
    }
}
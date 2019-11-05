package es.etologic.mahjongscoring2.app.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.*

class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    
    private val mFragments = ArrayList<Fragment>()
    private val mFragmentTitles = ArrayList<String>()
    
    fun addFragment(fragment: Fragment, title: String) {
        mFragments.add(fragment)
        mFragmentTitles.add(title)
    }
    
    override fun getItem(position: Int): Fragment {
        return mFragments[position]
    }
    
    override fun getCount(): Int {
        return mFragments.size
    }
    
    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitles[position]
    }
}

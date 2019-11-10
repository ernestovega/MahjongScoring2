package es.etologic.mahjongscoring2.app.game.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import es.etologic.mahjongscoring2.R
import es.etologic.mahjongscoring2.app.base.ViewPagerAdapter
import es.etologic.mahjongscoring2.app.game.activity.GameActivity
import es.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import es.etologic.mahjongscoring2.app.model.WiningHandStepsPages
import kotlinx.android.synthetic.main.game_activity.*

internal class WiningHandStepsDialogFragment : BaseGameDialogFragment() {
    
    companion object {
        private const val OFFSCREEN_PAGE_LIMIT = 1
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.wining_hand_steps_dialog_fragment, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }
    
    private fun setupViewPager() {
        val vpAdapter = initAdapter()
        tabLayoutGame.setupWithViewPager(viewPagerGame)
        viewPagerGame.offscreenPageLimit = OFFSCREEN_PAGE_LIMIT
        viewPagerGame.adapter = vpAdapter
        viewPagerGame.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageSelected(position: Int) {
                activityViewModel.setCurrentWiningHandStepsViewPagerPage(WiningHandStepsPages.getFromCode(position))
            }
        })
    }
    
    private fun initAdapter(): PagerAdapter? {
        val discarderDialogFragment = DiscarderDialogFragment()
        val pointsDialogFragment = PointsDialogFragment()
        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragment(discarderDialogFragment, getString(R.string.select_discarder))
        adapter.addFragment(pointsDialogFragment, getString(R.string.hand_points))
        return adapter
    }
}
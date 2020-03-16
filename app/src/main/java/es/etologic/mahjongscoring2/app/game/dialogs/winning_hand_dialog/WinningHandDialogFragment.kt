package es.etologic.mahjongscoring2.app.game.dialogs.winning_hand_dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import es.etologic.mahjongscoring2.R
import es.etologic.mahjongscoring2.app.base.ViewPagerAdapter
import es.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import es.etologic.mahjongscoring2.app.game.dialogs.HandActionsDialogFragment
import es.etologic.mahjongscoring2.app.game.dialogs.PointsDialogFragment
import kotlinx.android.synthetic.main.game_activity.*
import kotlinx.android.synthetic.main.wining_hand_steps_dialog_fragment.*
import javax.inject.Inject

internal class WinningHandDialogFragment : BaseGameDialogFragment() {
    
    companion object {
        private const val OFFSCREEN_PAGE_LIMIT = 1
    }
    @Inject
    internal lateinit var viewModelFactory: WinningHandDialogFragmentViewModelFactory
    private lateinit var viewModel: WinningHandDialogFragmentViewModel
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.wining_hand_steps_dialog_fragment, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupViewModel()
        viewModel.loadGame()
    }
    
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, viewModelFactory).get(WinningHandDialogFragmentViewModel::class.java)
        viewModel.getError().observe(viewLifecycleOwner, Observer(this::showError))
    }
    
    private fun setupViewPager() {
        siWiningHandStepsDialog?.setViewPager(viewPagerGame)
        siWiningHandStepsDialog?.stepCount = 1
        siWiningHandStepsDialog?.currentStep = 0
        val vpAdapter = initAdapter()
        tabLayoutGame?.setupWithViewPager(viewPagerGame)
        viewPagerGame?.offscreenPageLimit = OFFSCREEN_PAGE_LIMIT
        viewPagerGame?.adapter = vpAdapter
        viewPagerGame?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageSelected(position: Int) {
                viewModel.setCurrentPage(position)
            }
        })
    }
    
    private fun initAdapter(): PagerAdapter? {
        val handActionsDialogFragment = HandActionsDialogFragment()
        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragment(handActionsDialogFragment, getString(R.string.select_action))
        val pointsDialogFragment = PointsDialogFragment()
        adapter.addFragment(pointsDialogFragment, getString(R.string.hand_points))
        viewModel.setCurrentPage(0)
//        val discarderDialogFragment = DiscarderDialogFragment()
//        val pointsDialogFragment = PointsDialogFragment()
//        adapter.addFragment(discarderDialogFragment, getString(R.string.select_discarder))
//        adapter.addFragment(pointsDialogFragment, getString(R.string.hand_points))
        return adapter
    }
}
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
        internal const val TAG = "WinningHandDialogFragment"
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
        setupViewModel()
        setupViewPager()
        viewModel.loadGame()
    }
    
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, viewModelFactory).get(WinningHandDialogFragmentViewModel::class.java)
        viewModel.getError().observe(viewLifecycleOwner, Observer(this::showError))
    }
    
    private fun setupViewPager() {
        if (vpWiningHandStepsDialog != null) {
            val vpAdapter = initAdapter()
            tabLayoutGame?.setupWithViewPager(vpWiningHandStepsDialog)
            vpWiningHandStepsDialog?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
                override fun onPageScrollStateChanged(state: Int) {}
                override fun onPageSelected(position: Int) {
                    viewModel.setCurrentPage(position)
                }
            })
            siWiningHandStepsDialog?.stepCount = 2
            siWiningHandStepsDialog?.currentStep = 0
            vpWiningHandStepsDialog?.offscreenPageLimit = OFFSCREEN_PAGE_LIMIT
            vpWiningHandStepsDialog?.adapter = vpAdapter
            siWiningHandStepsDialog?.setViewPager(vpWiningHandStepsDialog)
        } else
            dismiss()
    }
    
    private fun initAdapter(): PagerAdapter? {
        val adapter = ViewPagerAdapter(childFragmentManager)
        val handActionsDialogFragment = HandActionsDialogFragment()
        val pointsDialogFragment = PointsDialogFragment()
        adapter.addFragment(handActionsDialogFragment, getString(R.string.select_action))
        adapter.addFragment(pointsDialogFragment, getString(R.string.points))
        viewModel.setCurrentPage(0)
        return adapter
    }
}
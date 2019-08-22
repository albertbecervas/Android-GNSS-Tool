package com.abecerra.gnssanalysis.presentation.ui.position


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abecerra.gnssanalysis.R
import com.abecerra.gnssanalysis.core.base.BaseFragment
import com.abecerra.gnssanalysis.core.computation.GnssServiceOutput
import com.abecerra.gnssanalysis.core.computation.data.PvtResponse
import com.abecerra.gnssanalysis.core.utils.extensions.showSelectedComputationSettingsAlert
import com.abecerra.gnssanalysis.core.utils.extensions.showStopAlert
import com.abecerra.gnssanalysis.presentation.ui.main.MainActivityInput
import com.abecerra.gnssanalysis.presentation.ui.map.MapFragment
import com.abecerra.pvt.computation.data.ComputationSettings
import kotlinx.android.synthetic.main.fragment_position.*
import timber.log.Timber

class PvtComputationFragment : BaseFragment(), MapFragment.MapListener, GnssServiceOutput.PvtListener {

    private var mActivityListener: MainActivityInput? = null

    private var mapFragment: MapFragment = MapFragment()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_position, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mActivityListener = context as? MainActivityInput
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
    }

    private fun setViews() {

        btComputeAction.setOnClickListener {
            if (isComputing()) {
                stopComputing()
            } else {
                onClickStartComputing()
            }
        }

        replaceFragment(R.id.mapFragmentContainer, mapFragment)

    }

    private fun onClickStartComputing() {
        val selectedModes = mPrefs.getSelectedModesList()
        //TODO change when selected modes returns non empty list
        if (selectedModes.isNotEmpty()) { // If no constellation or band has been selected
            context?.showSelectedComputationSettingsAlert {
                //navigate to settings
            }
        } else {
            startComputing(selectedModes)
        }

    }

    private fun startComputing(selectedModes: List<ComputationSettings>) {
        showMapLoading()
        mapFragment.clearMap()
        btComputeAction.text = getString(R.string.stop_computing)
        mActivityListener?.getGnssService()?.startComputing(selectedModes)
        mActivityListener?.getGnssService()?.let {
            it.bindPvtListener(this@PvtComputationFragment)
            it.startComputing(selectedModes)
        }
    }

    private fun stopComputing() {
        context?.showStopAlert {
            hideMapLoading()
            btComputeAction.text = getString(R.string.start_computing)
            btRecenter.visibility = View.GONE
            mActivityListener?.getGnssService()?.let {
                it.unbindPvtListener(this@PvtComputationFragment)
                it.stopComputing()
            }
        }
    }

    private fun isComputing(): Boolean = btComputeAction.text == getString(R.string.stop_computing)

    //Callbacks
    override fun onPvtResponse(pvtResponse: PvtResponse) {
        Timber.d("$FRAGMENT_TAG :::: pvtResponse received")
    }

    override fun onPvtError(error: String) {
        Timber.d("$FRAGMENT_TAG :::: pvtResponse error")
    }

    override fun onMapGesture() {
    }

    private fun showMapLoading() {
        pbMap?.visibility = View.VISIBLE
    }

    private fun hideMapLoading() {
        pbMap?.visibility = View.GONE
    }

    companion object {
        private const val FRAGMENT_TAG: String = "PvtComputationFragment"
    }

}
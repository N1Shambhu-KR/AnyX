package com.a.anyx.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import com.a.anyx.R
import com.a.anyx.activity.SendActivity
import com.a.anyx.fragment.base.BaseFragment
import com.a.anyx.interfaces.IOnFragment
import com.a.anyx.util.StateResolver
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView


class StateResolverFragment : BaseFragment() {

    private lateinit var stateResolver: StateResolver

    private lateinit var locationSetting: Button
    private lateinit var wifiSettings: Button
    private lateinit var bluetoothSettings: Button

    private var errorContainerColor: Int = 0
    private var errorColor: Int = 0
    private var primaryContainerColor: Int = 0
    private var primaryColor: Int = 0

    private lateinit var locationSettingsLauncher: ActivityResultLauncher<Intent>
    private lateinit var wifiSettingsLauncher: ActivityResultLauncher<Intent>
    private lateinit var bluetoothSettingsLauncher: ActivityResultLauncher<Intent>

    private lateinit var locationStateTextView: TextView

    private lateinit var wifiStateTextView: TextView

    private lateinit var bluetoothStateTextView: TextView

    private lateinit var letsGo: Button

    private lateinit var iOnFragment: IOnFragment

    override fun onAttach(context: Context) {
        super.onAttach(context)

        iOnFragment = context as IOnFragment
        iOnFragment.onFragment(this)
    }

    override fun getSelectedFilePathItems(): ArrayList<String>? {
        return null
    }

    override fun getTAG(): String? {
        return TAG
    }

    override fun onBackPressed(): Boolean {
        return true
    }

    override fun sortList(sortType: SelectorFragment.SortType, desc: Boolean) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        resolveRuntimeColors()

        locationSettingsLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
                object : ActivityResultCallback<ActivityResult> {

                    override fun onActivityResult(result: ActivityResult?) {

                        setState(
                            locationStateTextView,
                            stateResolver.getLocationState(),
                            if (stateResolver.getLocationState()) R.string.location_enabled else R.string.location_disabled
                        )

                    }

                })

        wifiSettingsLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
                object : ActivityResultCallback<ActivityResult> {

                    override fun onActivityResult(result: ActivityResult?) {

                        setState(
                            wifiStateTextView,
                            stateResolver.getWifiState(),
                            if (stateResolver.getWifiState()) R.string.wifi_enabled else R.string.wifi_disabled
                        )

                    }

                })

        bluetoothSettingsLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
                object : ActivityResultCallback<ActivityResult> {

                    override fun onActivityResult(result: ActivityResult?) {

                        setState(
                            bluetoothStateTextView,
                            stateResolver.getBluetoothState(),
                            if (stateResolver.getBluetoothState()) R.string.bluetooth_enabled else R.string.bluetooth_disabled
                        )

                    }

                })

        stateResolver = StateResolver(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_state_resolver, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        view.findViewById<MaterialToolbar>(R.id.toolbar).setOnClickListener {
            requireActivity().onBackPressed()
        }

        locationStateTextView =
            view.findViewById<TextView?>(R.id.state_resolver_location_state)

        wifiStateTextView = view.findViewById(R.id.state_resolver_wifi_state)

        bluetoothStateTextView = view.findViewById(R.id.state_resolver_bluetooth_state)

        locationSetting = view.findViewById<Button?>(R.id.state_resolver_location_settings).apply {

            (this as MaterialButton).icon = if (stateResolver.getLocationState()) ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_check_24) else ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_location_on_24)

            setState(
                locationStateTextView,
                stateResolver.getLocationState(),
                if (stateResolver.getLocationState()) R.string.location_enabled else R.string.location_disabled
            )

            setOnClickListener {

                locationSettingsLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        }

        wifiSettings = view.findViewById<Button?>(R.id.state_resolver_wifi_settings).apply {

            (this as MaterialButton).icon = if (stateResolver.getWifiState()) ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_check_24) else ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_wifi_24)

            setState(
                wifiStateTextView,
                stateResolver.getWifiState(),
                if (stateResolver.getWifiState()) R.string.wifi_enabled else R.string.wifi_disabled
            )

            setOnClickListener {
                wifiSettingsLauncher.launch(Intent(Settings.ACTION_WIFI_SETTINGS))
            }
        }

        bluetoothSettings = view.findViewById<Button?>(R.id.state_resolver_bluetooth_settings).apply {

            (this as MaterialButton).icon = if (stateResolver.getBluetoothState()) ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_check_24) else ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_bluetooth_24)

            setState(
                    bluetoothStateTextView,
                    stateResolver.getBluetoothState(),
                    if (stateResolver.getBluetoothState()) R.string.bluetooth_enabled else R.string.bluetooth_disabled
                )

                setOnClickListener {
                    bluetoothSettingsLauncher.launch(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                }
        }

        letsGo = view.findViewById<Button?>(R.id.state_resolver_lets_go).apply {

            isEnabled =
                stateResolver.getBluetoothState() && stateResolver.getWifiState() && stateResolver.getLocationState()

            setOnClickListener {

                if (requireActivity()::class.simpleName.equals(SendActivity::class.simpleName)){

                    requireActivity().supportFragmentManager.commit {

                        setCustomAnimations(
                            R.anim.enter_anim,
                            R.anim.exit_anim,
                            R.anim.pop_enter_anim,
                            R.anim.pop_exit_anim
                        )
                        addToBackStack(null)
                        replace(
                            R.id.activity_send_navigator,
                            WaitDeviceFragment(),WaitDeviceFragment.TAG
                        )
                    }
                }else{

                    requireActivity().supportFragmentManager.commit {

                        setCustomAnimations(
                            R.anim.enter_anim,
                            R.anim.exit_anim,
                            R.anim.pop_enter_anim,
                            R.anim.pop_exit_anim
                        )
                        addToBackStack(null)
                        replace(
                            R.id.activity_receive_navigator,
                            SearchDeviceFragment(),SearchDeviceFragment.TAG
                        )
                    }
                }
            }
        }

        view.viewTreeObserver.addOnWindowFocusChangeListener {
            if (it) {

                (locationSetting as MaterialButton).icon = if (stateResolver.getLocationState()) ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_check_24) else ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_location_on_24)

                setState(
                    locationStateTextView,
                    stateResolver.getLocationState(),
                    if (stateResolver.getLocationState()) R.string.location_enabled else R.string.location_disabled
                )

                (wifiSettings as MaterialButton).icon = if (stateResolver.getWifiState()) ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_check_24) else ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_wifi_24)

                setState(
                    wifiStateTextView,
                    stateResolver.getWifiState(),
                    if (stateResolver.getWifiState()) R.string.wifi_enabled else R.string.wifi_disabled
                )

                (bluetoothSettings as MaterialButton).icon = if (stateResolver.getBluetoothState()) ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_check_24) else ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_bluetooth_24)

                setState(
                    bluetoothStateTextView,
                    stateResolver.getBluetoothState(),
                    if (stateResolver.getBluetoothState()) R.string.bluetooth_enabled else R.string.bluetooth_disabled
                )

                letsGo.isEnabled = stateResolver.getBluetoothState() && stateResolver.getWifiState() && stateResolver.getLocationState()
            }
        }

    }


    private fun resolveRuntimeColors() {

        val runtimeValues = TypedValue()
        requireContext().theme.resolveAttribute(
            com.google.android.material.R.attr.colorErrorContainer,
            runtimeValues,
            true
        )
        errorContainerColor = runtimeValues.data
        requireContext().theme.resolveAttribute(
            com.google.android.material.R.attr.colorError,
            runtimeValues,
            true
        )
        errorColor = runtimeValues.data
        requireContext().theme.resolveAttribute(
            com.google.android.material.R.attr.colorPrimaryContainer,
            runtimeValues,
            true
        )
        primaryContainerColor = runtimeValues.data
        requireContext().theme.resolveAttribute(
            com.google.android.material.R.attr.colorPrimary,
            runtimeValues,
            true
        )
        primaryColor = runtimeValues.data
    }

    private fun setState(
        stateText: TextView,
        state: Boolean,
        resId: Int
    ) {

        if (state) {
            stateText.setText(resId)
            stateText.setTextColor(primaryColor)

        } else {

            stateText.setText(resId)
            stateText.setTextColor(errorColor)
        }
    }

    override fun loadImage(imageView: ImageView, position: Int) {

    }

    companion object {
        @JvmField
        val TAG: String = StateResolverFragment::class.simpleName!!
    }

}
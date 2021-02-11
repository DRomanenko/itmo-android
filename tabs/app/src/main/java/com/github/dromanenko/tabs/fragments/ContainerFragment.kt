package com.github.dromanenko.tabs.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.dromanenko.tabs.MainActivity
import com.github.dromanenko.tabs.R
import kotlinx.android.synthetic.main.fragment_container.*

class ContainerFragment : Fragment() {
    companion object {
        const val KEY_FOR_STR = "key_for_str"
    }

    private var qty = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (savedInstanceState != null) {
            qty = childFragmentManager.backStackEntryCount
        }
        return inflater.inflate(R.layout.fragment_container, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            activity?.title = it.getString(MainActivity.KEY_NAME)
            container_bn.setOnClickListener {
                val nestedKey = (1..qty).fold("0") { back, i -> "$back -> $i" }
                childFragmentManager
                    .beginTransaction()
                    .replace(R.id.container_fl, NestedFragment().apply {
                        arguments = Bundle().apply {
                            putString(KEY_FOR_STR, nestedKey)
                        }
                    }, nestedKey)
                    .addToBackStack(nestedKey)
                    .commit()
            }
        }

        childFragmentManager.addOnBackStackChangedListener {
            qty = childFragmentManager.backStackEntryCount
        }
    }
}
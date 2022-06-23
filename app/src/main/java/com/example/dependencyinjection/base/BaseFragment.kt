package com.example.dependencyinjection.base

import android.view.View
import android.widget.Toast
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.airbnb.lottie.LottieAnimationView
import com.example.dependencyinjection.utils.Status
import com.jintin.bindingextension.BindingFragment

open class BaseFragment<T : ViewBinding> : BindingFragment<T>() {

    fun navigate(destination: NavDirections) = with(findNavController()) {
        currentDestination?.getAction(destination.actionId)?.let { navigate(destination) }
    }

    protected fun initProgress(viewModel: BaseViewModel, progressBarId: Int) {
        viewModel.currentStatus.observe(viewLifecycleOwner) {
            it.message?.let { message -> Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show() }
            binding.root.findViewById<LottieAnimationView>(progressBarId)?.apply {
                when (it.status) {
                    Status.LOADING  -> startAnimation()
                    Status.SUCCESS  -> visibility = View.GONE
                    Status.NOTHING  -> visibility = View.GONE
                    Status.ERROR    -> visibility = View.GONE
                }
            }
        }
    }

    private fun LottieAnimationView.startAnimation() {
        progress = 0F
        visibility = View.VISIBLE
    }

}
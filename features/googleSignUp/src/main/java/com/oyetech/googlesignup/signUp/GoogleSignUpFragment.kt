package com.oyetech.googlesignup.signUp;

import com.oyetech.base.BaseFragment
import com.oyetech.extension.makeToast
import com.oyetech.googlesignup.R
import com.oyetech.googlesignup.databinding.FragmentGooglesignupBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
Created by Erdi Özbek
-23.06.2024-
-16:58-
 **/

class GoogleSignUpFragment : BaseFragment<FragmentGooglesignupBinding, GoogleSignUpVM>() {

    companion object {
        fun newInstance() = GoogleSignUpFragment()
    }

    override val viewModel: GoogleSignUpVM by viewModel()

    override val layoutId: Int
        get() = R.layout.fragment_googlesignup

    override fun prepareView() {

    }

    override fun setFragmentStaticViewClickAndText() {
        super.setFragmentStaticViewClickAndText()
        binding.tvGoogleSignUp.text = "Google Sign Up"
        binding.tvGoogleSignUp.setOnClickListener {
            viewModel.googleSignIn()
        }
    }

    override fun prepareObserver() {
        viewModel.signInOperationLiveData.observe(viewLifecycleOwner) {
            observeLoadingState(it)
            if (it.isSuccess) {
                makeToast("Success")
            } else if (it.isError) {
                makeToast("Error")
            }
        }
    }

}
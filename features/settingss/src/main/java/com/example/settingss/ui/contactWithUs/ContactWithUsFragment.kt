package com.example.settingss.ui.contactWithUs;

import androidx.core.widget.addTextChangedListener
import com.oyetech.base.BaseFragment
import com.oyetech.core.emailOperation.EmailValidationHelper
import com.oyetech.extension.hideKeyBoard
import com.oyetech.extension.makeToast
import com.oyetech.languageModule.keyset.WallpaperLanguage
import com.oyetech.materialViews.helper.viewHelpers.toolbarHelper.SimpleToolbarHelperLayout
import com.oyetech.materialViews.old.dialogs.dialogHelper.openFeedbackAlreadySentDialog
import com.oyetech.materialViews.old.ext.TextInputLayoutHelperExtensions.getInputLayoutString
import com.oyetech.settingss.R
import com.oyetech.settingss.databinding.FragmentContactWithUsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
Created by Erdi Özbek
-07/04/2024-
-16:14-
 **/

class ContactWithUsFragment : BaseFragment<FragmentContactWithUsBinding, ContactWithUsVM>() {

    companion object {
        fun newInstance() = ContactWithUsFragment()
    }

    override val viewModel: ContactWithUsVM by viewModel()

    override val layoutId: Int
        get() = R.layout.fragment_contact_with_us

    override fun prepareView() {
        SimpleToolbarHelperLayout.changeToolbarTitleCenter(
            binding.includeToolbar,
            title = WallpaperLanguage.CONTACT_WITH_US,
            center = true,
            backButtonEnable = true
        )
        prepareViewProperty()
    }

    override fun prepareObserver() {
        viewModel.feedbackSendOperationSingleLiveEvent.observe(viewLifecycleOwner) {
            observeLoadingState(it, disableToast = true)
            if (it.isSuccess) {
                viewModel.navigateUp()
                makeToast(WallpaperLanguage.FEEDBACK_SEND_SUCCESS_MESSAGE)
            } else if (it.isError) {
                this.openFeedbackAlreadySentDialog()
            }
        }
    }

    private fun prepareViewProperty() {
        binding.tilEmail.hint = WallpaperLanguage.FEEDBACK_EMAIL_HINT
        binding.tilBody.hint = WallpaperLanguage.FEEDBACK_BODY_HINT
        binding.tilHeader.hint = WallpaperLanguage.FEEDBACK_HEADER_HINT
        binding.btConfirm.text = WallpaperLanguage.SEND

        binding.btConfirm.setOnClickListener {
            controlAndSendForm()
            it.hideKeyBoard()
        }

        binding.tilEmail.editText?.addTextChangedListener {
            val emailString = binding.tilEmail.getInputLayoutString()
            val isEmailValid = EmailValidationHelper.isValidEmailSectionEvenForEmpty(emailString)
            if (!isEmailValid) {
                binding.tilEmail.error = WallpaperLanguage.FEEDBACK_EMAIL_ERROR
            } else {
                binding.tilEmail.error = null
            }
        }
    }

    private fun controlAndSendForm() {
        val emailInput = binding.tilEmail
        val headerInput = binding.tilHeader
        val bodyInput = binding.tilBody

        emailInput.error = null
        headerInput.error = null
        bodyInput.error = null

        val emailString = binding.tilEmail.getInputLayoutString()
        val isEmailValid = EmailValidationHelper.isValidEmailSectionEvenForEmpty(emailString)
        if (!isEmailValid) {
            binding.tilEmail.error = WallpaperLanguage.FEEDBACK_EMAIL_ERROR
            return
        } else {
            binding.tilEmail.error = null
        }

        val feedbackHeader = headerInput.getInputLayoutString()
        if (feedbackHeader.isBlank()) {
            headerInput.error = WallpaperLanguage.FEEDBACK_HEADER_ERROR
            return
        } else {
            headerInput.error = null
        }

        val feedbackBody = bodyInput.getInputLayoutString()
        if (feedbackBody.isBlank()) {
            bodyInput.error = WallpaperLanguage.FEEDBACK_BODY_ERROR
            return
        } else {
            bodyInput.error = null
        }

        viewModel.sendFeedback(emailString, feedbackHeader, feedbackBody)

    }

}
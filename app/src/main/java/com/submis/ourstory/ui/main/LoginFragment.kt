package com.submis.ourstory.ui.main


import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.submis.ourstory.R
import com.submis.ourstory.databinding.FragmentLoginBinding
import com.submis.ourstory.dom.Result
import com.submis.ourstory.ui.main.viewmodel.LoginViewModel
import com.submis.ourstory.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding
    private val viewModel by viewModels<LoginViewModel>()
    private val navArgs by navArgs<LoginFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            // Set up a click listener to login.
            btnAction.setOnClickListener {
                val email = edLoginEmail.text.toString()
                val password = edLoginPassword.text.toString()
                viewModel.login(email, password)
            }

            // Check if the passed arguments for email and password are not blank.
            if (navArgs.email.isNotBlank() and navArgs.password.isNotBlank()) {
                edLoginEmail.setText(navArgs.email)
                edLoginPassword.setText(navArgs.password)
            }

            // Set up a click listener for the Register text view that navigates to the register screen.
            tvRegister.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.signin_to_register)
            )
        }

        // Collect login results and handle different states (loading, success, error)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.loginResult.collect { result ->
                    when (result) {
                        is Result.Loading -> showLoading(true)
                        is Result.Success -> {
                            showLoading(false)
                            navigateToHome()
                        }
                        is Result.Error -> {
                            showLoading(false)
                            showMessage(result.message)
                        }
                    }
                }
            }
        }
    }

    // Navigate to the home activity and finish the current one
    private fun navigateToHome() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    // Show or hide the custom loading layout based on the isLoading flag
    private fun showLoading(isLoading: Boolean) {
        val loadingLayout = requireActivity().findViewById<View>(R.id.loading)
        loadingLayout.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    // Show a custom dialog with the provided message and alignment
    private fun showMessage(message: String?) {
        showDialogInfo(
            context = requireActivity(),
            message = message.toString(),
            alignment = Gravity.CENTER
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
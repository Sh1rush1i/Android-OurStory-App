package com.submis.ourstory.ui.main

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.submis.ourstory.R
import com.submis.ourstory.databinding.FragmentRegisterBinding
import com.submis.ourstory.dom.Result
import com.submis.ourstory.dom.auth.model.Register
import com.submis.ourstory.ui.main.viewmodel.RegisterViewModel
import com.submis.ourstory.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {

            // Set up a click listener for the Register button.
            btnRegister.setOnClickListener {
                showLoading(true)
                val name = edRegisterName.text.toString()
                val email = edRegisterEmail.text.toString()
                val password = edRegisterPassword.text.toString()
                viewModel.invokeRegister(name, email, password)
            }

            // Set up a click listener for the Sign In text view.
            tvSignIn.setOnClickListener {
                navigateToLogin()
            }
        }

        // Observe the registerResult LiveData in ViewModel
        viewModel.registerResult.observe(viewLifecycleOwner, registerValidate)
    }

    // Handling Form
    private val registerValidate = Observer<Result<Register>> { result ->
        when (result) {
            is Result.Loading -> showLoading(true)
            is Result.Success -> {
                showLoading(false)
                showMessage(result.data.message)

                val etEmail = binding?.edRegisterEmail?.text.toString().trim()
                val etPassword = binding?.edRegisterPassword?.text.toString().trim()
                navigateToLogin(etEmail, etPassword)
            }
            is Result.Error -> {
                showLoading(false)
                showMessage(result.message)
            }
        }
    }

    // Go to login fragment
    private fun navigateToLogin(email: String? = null, password: String? = null) {
        val intent = RegisterFragmentDirections.registerToSignin().also {
            it.email = email ?: ""
            it.password = password ?: ""
        }
        findNavController().navigate(intent)
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
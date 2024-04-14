package com.kanahia.web3jdemo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kanahia.web3jdemo.databinding.ActivityMainBinding
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.Web3ClientVersion
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Convert
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var clientVersion: Web3ClientVersion
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val web3j = Web3j.build(HttpService(getString(R.string.infura)))
        try {
            clientVersion = web3j.web3ClientVersion()
                .sendAsync().get()
            if (!clientVersion.hasError()) {
                Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, clientVersion.error.message, Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }

        binding.createButton.setOnClickListener {
            if (binding.passwordEditText.text.toString() != ""){
                try {
                    val password: String = binding.passwordEditText.text.toString()
                    val walletDirectory = filesDir.absolutePath
                    val walletName =
                        WalletUtils.generateNewWalletFile(password, File(walletDirectory))
                    binding.resultTextView.text = "Your wallet name is "+walletName
                    val credentials = WalletUtils.loadCredentials(password, walletDirectory + "/" + walletName);
                    binding.addressTextView.setText("Address: "+credentials.getAddress())
                    val ethGetBalance = web3j.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get()
                    binding.balanceTextView.setText("Balance: "+Convert.fromWei(ethGetBalance.getBalance().toString(), Convert.Unit.ETHER))
                    binding.passwordEditText.setText("")

                } catch (e: java.lang.Exception) {
                    Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }

    }
}
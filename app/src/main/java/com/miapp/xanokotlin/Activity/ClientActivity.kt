package com.miapp.xanokotlin.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.miapp.xanokotlin.Adaptador.ProductAdapter
import com.miapp.xanokotlin.R
import com.miapp.xanokotlin.SessionManager
import com.miapp.xanokotlin.api.ApiService
import com.miapp.xanokotlin.databinding.ActivityClientBinding
import kotlinx.coroutines.launch

class ClientActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClientBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var apiService: ApiService



}

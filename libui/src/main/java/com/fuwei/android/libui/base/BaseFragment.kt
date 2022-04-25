package com.fuwei.android.libui.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import java.lang.reflect.ParameterizedType

/**
 * Created by fuwei on 3/10/22.
 */
abstract class BaseFragment<T : ViewModel> : Fragment() {
    val TAG = "Fragment-" + this.javaClass.simpleName

    abstract fun getLayoutId(): Int
    abstract fun initView(view: View, bundle: Bundle?)
    abstract fun initListener()

    var mViewModel: T? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "onCreateView")
        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val type: ParameterizedType? = (javaClass.genericSuperclass as? ParameterizedType)
        type?.let {
            val clazz: Class<T>? = it.actualTypeArguments[0] as? Class<T>
            clazz?.let { it2 ->
                mViewModel = ViewModelProviders.of(this).get(it2)
            }
        }


        initView(view, arguments)
        initListener()

        if (mViewModel is BaseViewModel) {
            (mViewModel as BaseViewModel).start(arguments)
        } else if (mViewModel is BaseAndroidViewModel) {
            (mViewModel as BaseAndroidViewModel).start()
        }
        Log.i(TAG, "onViewCreated viewModel = $mViewModel")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")

    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mViewModel is BaseViewModel) {
            (mViewModel as BaseViewModel).stop()
        } else if (mViewModel is BaseAndroidViewModel) {
            (mViewModel as BaseAndroidViewModel).stop()
        }
        Log.i(TAG, "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")

    }
}
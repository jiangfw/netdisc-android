package com.fuwei.android.netdisc.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.fuwei.android.libui.base.BaseActivity
import com.fuwei.android.netdisc.R
import com.fuwei.android.netdisc.fragment.TestUnitFragment
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import kotlinx.android.synthetic.main.activity_home.*

/**
 * Created by fuwei on 4/24/22.
 */
class HomeActivity : BaseActivity(), View.OnClickListener {
    enum class HomeEnum(enumValue: Int) {
        TEST_UNIT(0),
        FILE_LIST(1),
        FILE_UPLOAD(2)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_home
    }

    private fun showFragment(enum: HomeEnum) {

        val data = Bundle()
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        when (enum) {
            HomeEnum.TEST_UNIT -> {
                data.putString("action", "test_unit")
                transaction.replace(
                    R.id.frame_layout,
                    TestUnitFragment.newInstance(data),
                    enum.toString()
                )
            }

            HomeEnum.FILE_LIST -> {
                data.putString("action", "file_list")
                transaction.replace(
                    R.id.frame_layout,
                    TestUnitFragment.newInstance(data),
                    enum.toString()
                )
            }

            HomeEnum.FILE_UPLOAD -> {
                data.putString("action", "file_upload")
                transaction.replace(
                    R.id.frame_layout,
                    TestUnitFragment.newInstance(data),
                    enum.toString()
                )
            }
        }

        transaction.commitAllowingStateLoss()

    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        requestRuntimePermissions()
        tv_home_prompt.setOnClickListener(this)
        btn_func_file_list.setOnClickListener(this)
        btn_func_file_upload.setOnClickListener(this)


        val mDisplayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(mDisplayMetrics)
        tv_home_prompt.text = "${tv_home_prompt.text}\n${mDisplayMetrics.toString()}"
        showFragment(HomeEnum.TEST_UNIT)

    }


    /**
     * 获取程序运行时权限
     */
    private fun requestRuntimePermissions() {
        XXPermissions.with(this)
            .permission(Permission.READ_PHONE_STATE)
            .permission(Permission.READ_EXTERNAL_STORAGE)
            .permission(Permission.WRITE_EXTERNAL_STORAGE)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: List<String>?, all: Boolean) {

                }

                override fun onDenied(permissions: List<String>?, never: Boolean) {

                }
            })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            tv_home_prompt.id -> {
                Toast.makeText(this, "Click Response.", Toast.LENGTH_SHORT).show()
            }

            btn_func_file_list.id -> {
                showFragment(HomeEnum.FILE_LIST)
            }

            btn_func_file_upload.id -> {
                showFragment(HomeEnum.FILE_UPLOAD)

            }
        }
    }

}
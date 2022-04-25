package com.fuwei.android.netdisc.fragment

import android.os.Bundle
import android.view.View
import com.alibaba.fastjson.TypeReference
import com.fuwei.android.libnetwork.data.BaseData
import com.fuwei.android.libui.base.BaseFragment
import com.fuwei.android.netdisc.R
import com.fuwei.android.netdisc.data.FileData
import com.fuwei.android.netdisc.utils.JSONFormatter
import com.fuwei.android.netdisc.viewmodel.TestUnitViewModel
import kotlinx.android.synthetic.main.fragment_unit_test.*

/**
 * Created by fuwei on 4/24/22.
 */
class TestUnitFragment : BaseFragment<TestUnitViewModel>() {
    companion object {
        fun newInstance(data: Bundle?): TestUnitFragment {
            val fragment = TestUnitFragment()
            if (data != null) fragment.arguments = data
            return fragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_unit_test
    }

    override fun initView(view: View, bundle: Bundle?) {
    }

    override fun initListener() {
        mViewModel?.action?.observe(viewLifecycleOwner) {
            val data =
                JSONFormatter.format(it, object : TypeReference<BaseData<List<FileData>>>() {}.type)

            tv_test_unit_content.text = data.toString()

        }
    }
}
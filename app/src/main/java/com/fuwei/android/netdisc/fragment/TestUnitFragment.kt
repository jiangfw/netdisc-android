package com.fuwei.android.netdisc.fragment

import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.fastjson.TypeReference
import com.fuwei.android.libnetwork.data.BaseData
import com.fuwei.android.libui.base.BaseFragment
import com.fuwei.android.netdisc.R
import com.fuwei.android.netdisc.adapter.FileExplorerRecyclerViewAdapter
import com.fuwei.android.netdisc.data.FileData
import com.fuwei.android.netdisc.data.FileItem
import com.fuwei.android.netdisc.data.RemoteItem
import com.fuwei.android.netdisc.utils.JSONFormatter
import com.fuwei.android.netdisc.viewmodel.TestUnitViewModel
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.android.synthetic.main.fragment_unit_test.*
import org.json.JSONException

/**
 * Created by fuwei on 4/24/22.
 */
class TestUnitFragment : BaseFragment<TestUnitViewModel>(),
    FileExplorerRecyclerViewAdapter.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private var recyclerViewAdapter: FileExplorerRecyclerViewAdapter? = null
    private var recyclerViewLinearLayoutManager: LinearLayoutManager? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null

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

        swipeRefreshLayout = view.findViewById(R.id.file_explorer_srl)
        swipeRefreshLayout?.setOnRefreshListener(this)
        swipeRefreshLayout?.isRefreshing = true

        val recyclerView: RecyclerView = view.findViewById(R.id.file_explorer_list)
        recyclerViewLinearLayoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = LandingAnimator()
        recyclerView.layoutManager = recyclerViewLinearLayoutManager
        val emptyFolderView = view.findViewById<View>(R.id.empty_folder_view)
        val noSearchResultsView = view.findViewById<View>(R.id.no_search_results_view)
        recyclerViewAdapter =
            FileExplorerRecyclerViewAdapter(context, emptyFolderView, noSearchResultsView, this)
        recyclerViewAdapter?.showThumbnails(false)
        recyclerViewAdapter?.setWrapFileNames(true)
        recyclerView.adapter = recyclerViewAdapter
    }

    override fun initListener() {
        mViewModel?.action?.observe(viewLifecycleOwner) {
            val data =
                JSONFormatter.format(it, object : TypeReference<BaseData<List<FileData>>>() {}.type)

            tv_test_unit_content.text = data.toString()

            if (data is BaseData<*>) {

                if (data.content is String) {

                } else {
                    include_file_explorer.visibility = View.VISIBLE
                    initData(data.content as List<FileData>)
                }


            }

        }
    }


    private fun initData(data: List<FileData>) {
        val fileItemList: MutableList<FileItem> = ArrayList()
        val remote = RemoteItem("fuwei", "webdav")
        for (item in data) {
            try {
                val filePath: String = item.path
                val fileName = item.name
                val fileSize = item.size
                val fileModTime = item.modTime
                val fileIsDir = item.isDir
                var mimeType = ""
                val extension = fileName.substring(fileName.lastIndexOf(".") + 1)
                val type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                if (type != null) {
                    mimeType = type
                }
                val fileItem =
                    FileItem(remote, filePath, fileName, fileSize, fileModTime, mimeType, fileIsDir)
                fileItemList.add(fileItem)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        swipeRefreshLayout?.isRefreshing = false
        recyclerViewAdapter?.newData(fileItemList)

    }

    override fun onFileClicked(fileItem: FileItem?) {
    }

    override fun onDirectoryClicked(fileItem: FileItem?, position: Int) {
    }

    override fun onFilesSelected() {
    }

    override fun onFileDeselected() {
    }

    override fun onFileOptionsClicked(view: View?, fileItem: FileItem?) {
    }

    override fun getThumbnailServerParams(): Array<String> {
        return arrayOf("http://106.12.132.116", "8088")

    }

    override fun onRefresh() {
        mViewModel?.fetchFileList()
    }


}
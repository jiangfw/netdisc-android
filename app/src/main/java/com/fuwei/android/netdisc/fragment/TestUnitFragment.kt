package com.fuwei.android.netdisc.fragment

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.fastjson.TypeReference
import com.fuwei.android.libcommon.logger.AILog
import com.fuwei.android.libnetwork.data.BaseData
import com.fuwei.android.libui.base.BaseFragment
import com.fuwei.android.netdisc.R
import com.fuwei.android.netdisc.adapter.FileExplorerRecyclerViewAdapter
import com.fuwei.android.netdisc.data.FileData
import com.fuwei.android.netdisc.data.FileItem
import com.fuwei.android.netdisc.data.RemoteItem
import com.fuwei.android.netdisc.utils.JSONFormatter
import com.fuwei.android.netdisc.viewmodel.TestUnitViewModel
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialOverlayLayout
import com.leinardi.android.speeddial.SpeedDialView
import com.leinardi.android.speeddial.SpeedDialView.OnActionSelectedListener
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
    private var fab: SpeedDialView? = null
    private var dir: String? = null


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

        dir = bundle?.getString("data", "")

        when (bundle?.getString("action", "")) {
            "file_list" -> {
                include_file_explorer.visibility = View.VISIBLE
            }
        }

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
        recyclerView.adapter = recyclerViewAdapter


        fab = view.findViewById(R.id.fab_fragment_file_explorer_list)
        fab?.overlayLayout = view.findViewById<View>(R.id.fab_overlay) as SpeedDialOverlayLayout
        fab?.setOnActionSelectedListener(OnActionSelectedListener { actionItem: SpeedDialActionItem ->
            when (actionItem.id) {
                R.id.fab_add_folder -> Toast.makeText(context, "todo add folder", Toast.LENGTH_LONG)
                    .show()
                R.id.fab_upload -> Toast.makeText(context, "todo upload file", Toast.LENGTH_LONG)
                    .show()
            }
            false
        })
        fab?.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_upload, R.drawable.ic_file_upload)
                .setLabel(getString(R.string.fab_upload_files))
                .create()
        )
        fab?.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_add_folder, R.drawable.ic_create_new_folder)
                .setLabel(getString(R.string.fab_new_folder))
                .create()
        )

        if (view.findViewById<View?>(R.id.background) != null) {
            view.findViewById<View>(R.id.background)
                .setOnClickListener { v: View? -> onClickOutsideOfView() }
        }
    }

    override fun initListener() {
        mViewModel?.action?.observe(viewLifecycleOwner) {
            val data =
                JSONFormatter.format(it, object : TypeReference<BaseData<List<FileData>>>() {}.type)

            tv_test_unit_content.text = data.toString()
            include_file_explorer.visibility = View.GONE

            if (data is BaseData<*> && data.content is List<*>) {
                val list = data.content as List<*>
                if (list.isNotEmpty() && list[0] is FileData) {
                    include_file_explorer.visibility = View.VISIBLE
                    initData(list as List<FileData>)
                }
            }

        }
    }

    private fun onClickOutsideOfView() {
        if (recyclerViewAdapter?.isInSelectMode == true) {
            recyclerViewAdapter?.cancelSelection()
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

    private fun showBottomBar() {
        val bottomBar = (context as FragmentActivity).findViewById<View>(R.id.bottom_bar)
        if (bottomBar.visibility == View.VISIBLE) {
            return
        }
        bottomBar.visibility = View.VISIBLE
        val animation = AnimationUtils.loadAnimation(context, R.anim.fade_in_animation)
        bottomBar.startAnimation(animation)
    }

    private fun hideBottomBar() {
        val bottomBar = (context as FragmentActivity).findViewById<View>(R.id.bottom_bar)
        if (bottomBar.visibility != View.VISIBLE) {
            return
        }
        val animation = AnimationUtils.loadAnimation(context, R.anim.fade_out_animation)
        bottomBar.animation = animation
        bottomBar.visibility = View.GONE
    }

    override fun onFileClicked(fileItem: FileItem?) {

        AILog.i(TAG, "onFileClicked fileItem = $fileItem")
    }

    override fun onDirectoryClicked(fileItem: FileItem?, position: Int) {
        AILog.i(TAG, "onDirectoryClicked fileItem = $fileItem")

    }

    override fun onFilesSelected() {
        val numOfSelected = recyclerViewAdapter!!.numberOfSelectedItems
        if (numOfSelected > 0) { // something is selected
            showBottomBar()
            fab!!.hide()
            fab!!.visibility = View.INVISIBLE
        }
    }

    override fun onFileDeselected() {
        if (recyclerViewAdapter?.isInSelectMode == false) {
            hideBottomBar()
            fab!!.show()
            fab!!.visibility = View.VISIBLE
        } else {
            onFilesSelected()
        }
    }

    override fun onFileOptionsClicked(view: View?, fileItem: FileItem?) {
        AILog.i(TAG, "onFileOptionsClicked fileItem = $fileItem")
    }

    override fun getThumbnailServerParams(): Array<String> {
        return arrayOf("http://106.12.132.116", "8088")

    }

    override fun onRefresh() {
        mViewModel?.fetchFileList(dir)
    }


}
package com.ecjtu.heaven.ui.adapter

import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.Target
import com.ecjtu.heaven.R
import com.ecjtu.netcore.model.PageModel

/**
 * Created by Ethan_Xiang on 2017/9/8.
 */
class CardListAdapter(var pageModel: PageModel) : RecyclerView.Adapter<CardListAdapter.VH>(), RequestListener<Bitmap> {

    private var mLastHeight = 0

    override fun getItemCount(): Int {
        return pageModel.itemList.size
    }

    override fun onBindViewHolder(holder: VH?, position: Int) {
        val context = holder?.itemView?.context
        val params = holder?.itemView?.layoutParams
        if (mLastHeight != 0) {
            params?.height = mLastHeight // 防止上滑时 出现跳动的情况
        } else {
            params?.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300f, context?.resources?.displayMetrics).toInt()
        }

        val imageView = holder?.mImageView
        val options = RequestOptions()
        options.centerCrop()
        val url = pageModel.itemList[position].imgUrl /*thumb2OriginalUrl(pageModel.itemList[position].imgUrl)*/
        val builder = LazyHeaders.Builder().addHeader("User-Agent","Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Mobile Safari/537.36")
                .addHeader("Accept","image/webp,image/apng,image/*,*/*;q=0.8")
                .addHeader("Accept-Encoding","gzip, deflate")
                .addHeader("Accept-Language","zh-CN,zh;q=0.8")
                .addHeader("Host","i.meizitu.net")
                .addHeader("Proxy-Connection","keep-alive")
                .addHeader("Referer","http://m.mzitu.com/")
        val glideUrl = GlideUrl(url,builder.build())
        url.let {
            Glide.with(context).asBitmap().load(glideUrl).listener(this).apply(options).into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VH {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.layout_card_view, parent, false)
        return VH(v)
    }

    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
        return false
    }

    override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
        if (target is BitmapImageViewTarget) {
            val layoutParams = (target.view.parent?.parent as View).layoutParams
            val height = resource?.height ?: LinearLayout.LayoutParams.WRAP_CONTENT
            if (layoutParams.height != height) {
                layoutParams.height = height
            }

            target.view.setImageBitmap(resource)

            mLastHeight = height
        }
        return true
    }

    private fun thumb2OriginalUrl(url: String): String? {
        return try {
            var localUrl = url.replace("/thumbs", "")
            var suffix = localUrl.substring(localUrl.lastIndexOf("/"))
            suffix = suffix.substring(suffix.indexOf("_") + 1)
            var end = suffix.substring(suffix.lastIndexOf("."))
            suffix = suffix.substring(0, suffix.lastIndexOf("_"))
            suffix += end
            localUrl.substring(0, localUrl.lastIndexOf("/") + 1) + suffix
        } catch (ex: Exception) {
            null
        }
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mImageView = itemView.findViewById(R.id.image) as ImageView

        init {
            mImageView?.adjustViewBounds = true
        }
    }
}
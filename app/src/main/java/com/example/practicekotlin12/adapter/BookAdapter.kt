package com.example.practicekotlin12.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practicekotlin12.databinding.ItemBookBinding
import com.example.practicekotlin12.model.Book

class BookAdapter(private val itemClickedListener: (Book) -> Unit): ListAdapter<Book, BookAdapter.BookItemViewHolder>(diffUtil) { // diffUtil: 중복되는 값을 할당것인지에 대한 여부를 판단하는 것

    inner class BookItemViewHolder(private val binding: ItemBookBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(bookModel: Book) { // Book에 저장되어 있는 데이터를 가져오는 함수
            binding.titleTextView.text = bookModel.title
            binding.descriptionTextView.text = bookModel.description

            binding.root.setOnClickListener {
                itemClickedListener(bookModel)
            }

            Glide
                .with(binding.coverImageView.context) // context를 가져옴
                .load(bookModel.coverSmallUrl) // url 로드
                .into(binding.coverImageView) // 이미지 뷰에 가져온 이미지 삽입
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookItemViewHolder {
        return BookItemViewHolder(ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: BookItemViewHolder, position: Int) {
        holder.bind(currentList[position]) // ListAdapter 의 데이터 리스트들은 currentList로 저장되어있다. 메서드는 위의 bind()로 전달
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Book>() {
            override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean { // 아이템이 같은지
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean { // 내부 컨텐츠가 같은지
                return oldItem.id == newItem.id
            }

        }
    }
}

/* ItemBookBinding을 상속할 수 있는 이유는 gradle단에서의 binding 활성화와 레이아웃 item_book.xml(이름도 일치해야함) 이름의 파일을 생성하였기 때문이다.
   ViewHolder: recycler뷰의 경우 미리 만들어둔 뷰(틀)에 데이터만 넣는데, 이때 만들어둔 뷰에 해당하는 것이 ViewHolder이다.
 */
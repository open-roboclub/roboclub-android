package amu.roboclub.news;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import amu.roboclub.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsFragment extends Fragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.no_news)
    LinearLayout loading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_news, container, false);

        ButterKnife.bind(this, root);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        DatabaseReference newsReference = FirebaseDatabase.getInstance().getReference("news");

        FirebaseRecyclerOptions<News> options = new FirebaseRecyclerOptions.Builder<News>()
                .setQuery(newsReference, News.class)
                .setLifecycleOwner(this)
                .build();

        FirebaseRecyclerAdapter newsAdapter = new FirebaseRecyclerAdapter<News, NewsHolder>(options) {

            @NonNull
            @Override
            public NewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_announcement, parent, false);

                return new NewsHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull NewsHolder holder, int position, @NonNull News news) {
                if (loading.getVisibility() == View.VISIBLE)
                    loading.setVisibility(View.GONE);

                holder.setNews(getContext(), news);
            }

        };

        recyclerView.setAdapter(newsAdapter);

        return root;
    }
}

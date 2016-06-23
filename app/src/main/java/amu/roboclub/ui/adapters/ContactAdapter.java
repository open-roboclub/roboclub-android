package amu.roboclub.ui.adapters;

import amu.roboclub.R;
import amu.roboclub.models.Contact;
import amu.roboclub.utils.CircleTransform;
import android.content.Context;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private Context context;
    private List<Contact> contacts;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CardView root;
        public TextView name, designation;
        public ImageView avatar;

        public ViewHolder(View view) {
            super(view);
            root = (CardView) view.findViewById(R.id.rootView);
            name = (TextView) view.findViewById(R.id.name);
            designation = (TextView) view.findViewById(R.id.designation);
            avatar = (ImageView) view.findViewById(R.id.avatar);
        }
    }

    public ContactAdapter(Context context, List<Contact> contacts){
        this.context = context;
        this.contacts = contacts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Contact contact = contacts.get(position);
        holder.name.setText(contact.name);
        holder.designation.setText(contact.designation);

        Picasso.with(context).load(contact.imgUrl)
                .placeholder(VectorDrawableCompat.create(context.getResources(), R.drawable.ic_avatar, null))
                .transform(new CircleTransform())
                .into(holder.avatar);

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

}
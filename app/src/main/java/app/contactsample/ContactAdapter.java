package app.contactsample;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import app.sectionfastscroll.fastscroll.FastScrollerLabelPublisher;
import app.contactsample.databinding.ContactsListItemBinding;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> implements FastScrollerLabelPublisher {
    private List<Contact> mList;

    public ContactAdapter(List<Contact> list) {
        mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        ContactsListItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.contacts_list_item, viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Contact contact = mList.get(i);
        viewHolder.binding.setData(contact);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public String getLabel(int index) {
        return mList.get(index).number.substring(0, 1).toUpperCase();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ContactsListItemBinding binding;

        public ViewHolder(@NonNull ContactsListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

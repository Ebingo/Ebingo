package com.promote.ebingo.find;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.promote.ebingo.R;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FindFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FindFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private GridView fragfindgv;
    private LinearLayout searchll;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindFragment.
     */
    public static FindFragment newInstance(String param1, String param2) {
        FindFragment fragment = new FindFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FindFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_find, container, false);
        initialize(view);
        return view;

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void initialize(ViewGroup view) {
        searchll = (LinearLayout) view.findViewById(R.id.search_ll);
        fragfindgv = (GridView) view.findViewById(R.id.frag_find_gv);

        String[] categorys = getResources().getStringArray(R.array.category_data);
        MyGrideAdapter categoryAdapter = new MyGrideAdapter(categorys, getActivity().getApplicationContext());
        fragfindgv.setAdapter(categoryAdapter);
    }

    /**
     * category gridview adapter.
     */
    private class MyGrideAdapter extends BaseAdapter{

        String[] categorys = null;
        Context context = null;

        public MyGrideAdapter(String[] categorys, Context context){

            this.categorys = categorys;
            this.context = context;
        }


        @Override
        public int getCount() {
            return categorys.length;
        }

        @Override
        public Object getItem(int position) {
            return categorys[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null){

                holder = new ViewHolder();
                convertView = (View) LayoutInflater.from(this.context).inflate(R.layout.find_gridview_item, null);
                holder.imgView = (ImageView) convertView.findViewById(R.id.find_grid_item_img);
                holder.text = (TextView)convertView.findViewById(R.id.find_grid_item_tv);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder)convertView.getTag();
            }

            holder.text.setText(categorys[position]);

            return convertView;
        }

    }


    static class ViewHolder{
        ImageView imgView;
        TextView text;
    }

}

package pe.edu.tecsup.appsoporte.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;

import java.util.List;

import pe.edu.tecsup.appsoporte.R;
import pe.edu.tecsup.appsoporte.adapters.IncidentRVAdapter;
import pe.edu.tecsup.appsoporte.models.APIError;
import pe.edu.tecsup.appsoporte.models.Incident;
import pe.edu.tecsup.appsoporte.services.TecsupService;
import pe.edu.tecsup.appsoporte.services.TecsupServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncidentFragment extends Fragment {

    private static final String TAG = IncidentFragment.class.getSimpleName();

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private View emptyView;

    private static final String ARG_STATUS = "status";
    private String status;

    public static Fragment newInstance(String status){
        Fragment fragment = new IncidentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = getArguments().getString(ARG_STATUS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().setTitle(R.string.title_pending);
        View view = inflater.inflate(R.layout.fragment_incident, container, false);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        initialize();
                    }
                }
        );

        recyclerView = (RecyclerView)view.findViewById(R.id.incidents_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new IncidentRVAdapter(getActivity()));

        emptyView = view.findViewById(R.id.empty_message);

        // ProgressBar Visible
        getActivity().findViewById(R.id.main_progress).setVisibility(View.VISIBLE);

        initialize();

        return view;
    }

    private void initialize() {

        TecsupService service = TecsupServiceGenerator.createService(TecsupService.class);

        Call<List<Incident>> call = service.getIncidents(status);

        call.enqueue(new Callback<List<Incident>>() {
            @Override
            public void onResponse(Call<List<Incident>> call, Response<List<Incident>> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        List<Incident> incidents = response.body();
                        Log.d(TAG, "incidents: " + incidents);

                        IncidentRVAdapter adapter = (IncidentRVAdapter) recyclerView.getAdapter();
                        adapter.setIncidents(incidents);
                        adapter.notifyDataSetChanged();

                        if(incidents.isEmpty()){
                            emptyView.setVisibility(View.VISIBLE);
                        }

                        // Stop refreshing
                        refreshLayout.setRefreshing(false);

                        // ProgressBar Gone
                        getActivity().findViewById(R.id.main_progress).setVisibility(View.GONE);

                    } else {
                        APIError error = TecsupServiceGenerator.parseError(response);
                        Log.e(TAG, "onError: " + error);
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<List<Incident>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                if(getActivity()!=null) Toast.makeText(getActivity(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
            }

        });
    }

    /**
     * Refreshing on notifyReceiver
     */

    final BroadcastReceiver notifyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive notifyReceiver...");
            try {
                initialize();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    };

    @Override
    public void onResume() {
        getContext().registerReceiver(notifyReceiver, new IntentFilter("com.google.android.c2dm.intent.RECEIVE"));
        super.onResume();
    }

    @Override
    public void onPause() {
        getContext().unregisterReceiver(notifyReceiver);
        super.onPause();
    }

}

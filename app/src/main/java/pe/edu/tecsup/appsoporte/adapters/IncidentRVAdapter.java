package pe.edu.tecsup.appsoporte.adapters;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import pe.edu.tecsup.appsoporte.R;
import pe.edu.tecsup.appsoporte.fragments.IncidentFragment;
import pe.edu.tecsup.appsoporte.models.APIError;
import pe.edu.tecsup.appsoporte.models.Incident;
import pe.edu.tecsup.appsoporte.services.TecsupService;
import pe.edu.tecsup.appsoporte.services.TecsupServiceGenerator;
import pe.edu.tecsup.appsoporte.util.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ebenites on 19/01/2017.
 * Tutorial: https://code.tutsplus.com/es/tutorials/getting-started-with-recyclerview-and-cardview-on-android--cms-23465
 * https://github.com/tutsplus/Android-CardViewRecyclerView
 */

public class IncidentRVAdapter extends Adapter<IncidentRVAdapter.ViewHolder> {

    private static final String TAG = IncidentRVAdapter.class.getSimpleName();

    private FragmentActivity activity;

    private List<Incident> incidents;

    private ProgressDialog progressDialog;

    public void setIncidents(List<Incident> incidents) {
        this.incidents = incidents;
    }

    public IncidentRVAdapter(FragmentActivity activity){
        this.activity = activity;
        this.incidents = new ArrayList<>();

        progressDialog = new ProgressDialog(activity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View flagView;
        TextView statusText;
        CircleImageView customerImage;
        TextView customerNameText;
        TextView customerPhoneText;
        TextView locationText;
        RelativeTimeTextView dateText;
        View technicalView;
        TextView technicalText;
        Button attendButton;
        Button callButton;
        Button closeButton;

        ViewHolder(View itemView) {
            super(itemView);
            flagView = itemView.findViewById(R.id.incident_flag);
            statusText = itemView.findViewById(R.id.incident_status);
            customerImage = itemView.findViewById(R.id.incident_customer_picture);
            customerNameText = itemView.findViewById(R.id.incident_customer_name);
            customerPhoneText = itemView.findViewById(R.id.incident_customer_phonenumber);
            locationText = itemView.findViewById(R.id.incident_location);
            dateText = itemView.findViewById(R.id.incident_date);
            technicalView = itemView.findViewById(R.id.incident_technical_block);
            technicalText = itemView.findViewById(R.id.incident_technical);
            attendButton = itemView.findViewById(R.id.attend_button);
            callButton = itemView.findViewById(R.id.call_button);
            closeButton = itemView.findViewById(R.id.close_button);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_incident, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        final Incident incident = incidents.get(position);

        if(Constants.INCIDENT_STATUS_PENDIENT.equals(incident.getStatus())){
            viewHolder.flagView.setBackgroundColor(ContextCompat.getColor(activity, R.color.gold));
            viewHolder.statusText.setText(R.string.title_pending);
            viewHolder.technicalView.setVisibility(View.GONE);
            viewHolder.callButton.setVisibility(View.GONE);
            viewHolder.closeButton.setVisibility(View.GONE);
        }else if(Constants.INCIDENT_STATUS_ATENTION.equals(incident.getStatus())){
            viewHolder.flagView.setBackgroundColor(ContextCompat.getColor(activity, R.color.green));
            viewHolder.statusText.setText(R.string.title_attending);
            viewHolder.attendButton.setVisibility(View.GONE);
        }else if(Constants.INCIDENT_STATUS_CLOSED.equals(incident.getStatus())){
            viewHolder.flagView.setBackgroundColor(ContextCompat.getColor(activity, R.color.gris));
            viewHolder.statusText.setText(R.string.title_closed);
            viewHolder.attendButton.setVisibility(View.GONE);
            viewHolder.callButton.setVisibility(View.GONE);
            viewHolder.closeButton.setVisibility(View.GONE);
        }

        String url = TecsupServiceGenerator.PHOTO_URL + incident.getCustomerid() + "/photo.jpg";
        Picasso.with(activity).load(url).into(viewHolder.customerImage);

        viewHolder.customerNameText.setText(incident.getCustomer());
        viewHolder.customerPhoneText.setText(incident.getPhone());

        viewHolder.locationText.setText(incident.getLocation());

        // https://github.com/curioustechizen/android-ago
        viewHolder.dateText.setReferenceTime(incident.getCreated().getTime());

        viewHolder.technicalText.setText(incident.getTechnical());

        viewHolder.attendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setMessage("Tomando la atención...");
                progressDialog.show();

                TecsupService service = TecsupServiceGenerator.createService(TecsupService.class);

                Call<Incident> call = service.updateIncident(incident.getId(), Constants.INCIDENT_STATUS_ATENTION);

                call.enqueue(new Callback<Incident>() {
                    @Override
                    public void onResponse(Call<Incident> call, Response<Incident> response) {
                        try {

                            int statusCode = response.code();
                            Log.d(TAG, "HTTP status code: " + statusCode);

                            if (response.isSuccessful()) {

                                Incident incident = response.body();
                                Log.d(TAG, "incident: " + incident);

                                // Got to IncidentFragment with INCIDENT_STATUS_ATENTION
                                activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_content, IncidentFragment.newInstance(Constants.INCIDENT_STATUS_ATENTION)).commit();

                                Toast.makeText(activity, "¡Haz tomado el ticket!", Toast.LENGTH_LONG).show();

                            } else {
                                APIError error = TecsupServiceGenerator.parseError(response);
                                Log.e(TAG, "onError: " + error);

                                // Got to IncidentFragment with INCIDENT_STATUS_PENDIENT
                                activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_content, IncidentFragment.newInstance(Constants.INCIDENT_STATUS_PENDIENT)).commit();

                                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        } catch (Throwable t) {
                            try {
                                Log.e(TAG, "onThrowable: " + t.toString(), t);
                                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_LONG).show();
                            }catch (Throwable x){}
                        }finally {
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<Incident> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.toString());
                        if(activity!=null) Toast.makeText(activity, activity.getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }

                });

            }
        });

        viewHolder.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(activity)
                        .setMessage("¿Seguro que desea cerrar la atención?")
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                progressDialog.setMessage("Cerrando atención...");
                                progressDialog.show();

                                TecsupService service = TecsupServiceGenerator.createService(TecsupService.class);

                                Call<Incident> call = service.updateIncident(incident.getId(), Constants.INCIDENT_STATUS_CLOSED);

                                call.enqueue(new Callback<Incident>() {
                                    @Override
                                    public void onResponse(Call<Incident> call, Response<Incident> response) {
                                        try {

                                            int statusCode = response.code();
                                            Log.d(TAG, "HTTP status code: " + statusCode);

                                            if (response.isSuccessful()) {

                                                Incident incident = response.body();
                                                Log.d(TAG, "incident: " + incident);

                                                // Got to IncidentFragment with INCIDENT_STATUS_ATENTION
                                                activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_content, IncidentFragment.newInstance(Constants.INCIDENT_STATUS_ATENTION)).commit();

                                                Toast.makeText(activity, "¡Haz cerrado el ticket!", Toast.LENGTH_LONG).show();

                                            } else {
                                                APIError error = TecsupServiceGenerator.parseError(response);
                                                Log.e(TAG, "onError: " + error);
                                                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_LONG).show();
                                            }

                                        } catch (Throwable t) {
                                            try {
                                                Log.e(TAG, "onThrowable: " + t.toString(), t);
                                                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_LONG).show();
                                            }catch (Throwable x){}
                                        }finally {
                                            progressDialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Incident> call, Throwable t) {
                                        Log.e(TAG, "onFailure: " + t.toString());
                                        if(activity!=null) Toast.makeText(activity, activity.getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    }

                                });

                            }
                        }).create().show();

            }
        });

        viewHolder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, 100);
                    return;
                }

                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + incident.getPhone()));
                activity.startActivity(intent);
            }
        }) ;

    }

    @Override
    public int getItemCount() {
        return this.incidents.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}

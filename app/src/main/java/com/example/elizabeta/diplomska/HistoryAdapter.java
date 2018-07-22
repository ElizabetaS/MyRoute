package com.example.elizabeta.diplomska;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elizabeta.diplomska.Model.User;
import com.example.elizabeta.diplomska.Model.UserRResponse;
import com.example.elizabeta.diplomska.Model.UserRoute;
import com.example.elizabeta.diplomska.Model.UserRouteResponse;
import com.example.elizabeta.diplomska.api.RestApi;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    Listener listener;
    Context context;
    RestApi api;
    public ArrayList<UserRouteResponse> userRouteList = new ArrayList<>();

    public HistoryAdapter(Context _context, ArrayList<UserRouteResponse> model, Listener _listener)
    {
        listener = _listener;
        context = _context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view= inflater.inflate(R.layout.rec_view_row,parent,false);
        HistoryAdapter.ViewHolder viewHolder= new HistoryAdapter.ViewHolder(view);
        return viewHolder;    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        api = new RestApi(context);
        final UserRouteResponse userRoute = userRouteList.get(position);
        holder.routeName.setText(userRoute.name.toString());
        String date = userRoute.startTime.substring(0,10);
        String time = userRoute.startTime.substring(11,16);
        holder.start.setText(date + " " + time);
        String dateEnd = userRoute.endTime.substring(0,10);
        String timeEnd = userRoute.endTime.substring(11,16);
        holder.finish.setText(dateEnd + " " + timeEnd);
        holder.speed.setText(String.valueOf(userRoute.speed)+"km/h");
        holder.distance.setText(String.valueOf(String.format("%.2f",userRoute.distance)));

        holder.routeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,SeeHisttoryMap.class);
                intent.putExtra("Route",userRoute.uid);
                context.startActivity(intent);
            }
        });
        holder.routeName.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                if (userRoute.uid != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Are you sure you want to delete route?")
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id){
                                            Call<Void> call = api.deleteRoute(userRoute.uid);
                                            call.enqueue(new Callback<Void>() {
                                                @Override
                                                public void onResponse(Call<Void> call, Response<Void> response) {
                                                    if (response.code() == 200) {
                                                        userRouteList.remove(position);
                                                            Intent intent = new Intent(context, MyRoutes.class);
                                                            context.startActivity(intent);

                                                    } else if (response.code() == 401) {
                                                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                                @Override
                                                public void onFailure(Call<Void> call, Throwable t) {
                                                    Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                    return true;
            }
        });

    }
    public void setItems(ArrayList<UserRouteResponse> userList)
    {
        userRouteList = userList;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        if(userRouteList.size()>0)
        {
            return  userRouteList.size();
        }
        else return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.routeName)
        TextView routeName;
        @BindView(R.id.startRoute)
        TextView start;
        @BindView(R.id.finishRoute)
        TextView finish;
        @BindView(R.id.speed)
        TextView speed;
        @BindView(R.id.distance)
        TextView distance;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}

// app/src/main/java/com/yanmir/applacteossanjose/ProductoAdapter.java
package com.yanmir.applacteossanjose;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class ProductoAdapter extends ArrayAdapter<Producto> {

    public ProductoAdapter(@NonNull Context context, @NonNull List<Producto> productos) {
        super(context, 0, productos);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_producto, parent, false);
        }

        Producto producto = getItem(position);

        TextView txtNombre = itemView.findViewById(R.id.txtNombre);
        TextView txtFecha  = itemView.findViewById(R.id.txtFecha);
        TextView txtLote   = itemView.findViewById(R.id.txtVida);

        if (producto != null) {
            txtNombre.setText(producto.getTipoProducto());
            txtFecha.setText(getContext().getString(R.string.fecha_placeholder, producto.getFechaProduccion()));
            txtLote.setText(getContext().getString(R.string.lote_placeholder, producto.getLote()));
        }

        return itemView;
    }
}
package com.yanmir.applacteossanjose;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ProductoAdapter extends ArrayAdapter<Producto> {

    public ProductoAdapter(Context context, List<Producto> productos) {
        super(context, 0, productos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Reusar la vista si ya existe, si no, inflar una nueva
        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_producto, parent, false);
        }

        // Obtener el producto de la posición actual
        Producto producto = getItem(position);

        // Referenciar los TextView del layout item_producto.xml
        TextView txtNombre = itemView.findViewById(R.id.txtNombre);
        TextView txtFecha  = itemView.findViewById(R.id.txtFecha);
        TextView txtLote   = itemView.findViewById(R.id.txtVida);  // Si lo usas para mostrar lote

        // Rellenar con los datos del producto
        txtNombre.setText(producto.getTipoProducto());
        txtFecha.setText("Fecha: " + producto.getFechaProduccion());
        txtLote.setText("Lote: " + producto.getLote());

        return itemView;
    }
}

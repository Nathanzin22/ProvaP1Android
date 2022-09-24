package com.software.provap1;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.Toast;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.software.Provap1.BancoDeDados.AbastecimentoDB;
import com.software.Provap1.BancoDeDados.DBHelper;
import com.software.Provap1.Entidades.Abastecimento;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText campoQuilometroAtual;
    EditText campoQuantidadeLitros;
    EditText campoData;
    EditText campoValor;
    TextView campoConsumo;

    Button botaoSalvar;

    List<Abastecimento> listaAbastecimento;
    ListView listaDados;
    ArrayAdapter adapter;

    AbastecimentoDB abastecimentoDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBHelper db = new DBHelper(MainActivity.this);
        abastecimentoDB = new AbastecimentoDB(db);

        campoQuilometroAtual = findViewById(R.id.campoQuilometragem);
        campoQuantidadeLitros = findViewById(R.id.campoQuantidadeLitros);
        campoData = findViewById(R.id.campoData);
        campoValor = findViewById(R.id.campoValor);
        campoConsumo = findViewById(R.id.campoConsumo);
        listaDados = findViewById(R.id.listaAbastecimento);

        botaoSalvar = findViewById(R.id.botaoSalvar);

        listaAbastecimento = new ArrayList<>();
        adapter = new ArrayAdapter<>(MainActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, listaAbastecimento);
        listaDados.setAdapter(adapter);
        abastecimentoDB.listar(listaAbastecimento);

        calcularConsumo();
        acoesComponentes();
    }

    private void calcularConsumo() {
        Float consumo = 0f;
        Float quilometragemRodada = 0f;
        Float quantidadeAbastecida = 0f;

        if (listaAbastecimento.size() != 1) {
            for (int i = 0; i < listaAbastecimento.size(); i++) {
                if (i + 1 < listaAbastecimento.size()) {
                    quilometragemRodada += listaAbastecimento.get(i + 1).getQuilometragemAtual() - listaAbastecimento.get(i).getQuilometragemAtual();
                }

                quantidadeAbastecida += listaAbastecimento.get(i).getQuantidadeAbastecida();
            }

            consumo = quantidadeAbastecida / quilometragemRodada;
        } else {
            consumo = 0f;
        }

        campoConsumo.setText(String.format("%.2f Litro/KM | %.2f | %.2f", consumo, quilometragemRodada, quantidadeAbastecida));
    }

    private void acoesComponentes() {
        listaDados.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                new AlertDialog.Builder(view.getContext())
                        .setMessage("Deseja realmente remover?")
                        .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int j) {
                                abastecimentoDB.remover(listaAbastecimento.get(i).getId());

                                abastecimentoDB.listar(listaAbastecimento);
                                adapter.notifyDataSetChanged();

                                Toast.makeText(MainActivity.this, "Removido com Sucesso!", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .create().show();
                return (false);
            }
        });

        botaoSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Abastecimento abastecimento = new Abastecimento();

                abastecimento.setQuilometragemAtual(Float.parseFloat(campoQuilometroAtual.getText().toString()));
                abastecimento.setQuantidadeAbastecida(Float.parseFloat(campoQuantidadeLitros.getText().toString()));
                abastecimento.setData(campoData.getText().toString());
                abastecimento.setValor(Float.parseFloat(campoValor.getText().toString()));

                abastecimentoDB.inserir(abastecimento);
                abastecimentoDB.listar(listaAbastecimento);
                adapter.notifyDataSetChanged();
                calcularConsumo();
            }
        });
    }
}
package es.etologic.mahjongscoring2.app.new_game;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.ChipInterface;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.etologic.mahjongscoring2.Injector;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.app.game.GameActivity;
import es.etologic.mahjongscoring2.app.model.ShowState;
import es.etologic.mahjongscoring2.app.utils.StringUtils;
import es.etologic.mahjongscoring2.domain.entities.Player;

import static android.support.design.widget.Snackbar.LENGTH_INDEFINITE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static es.etologic.mahjongscoring2.app.model.ShowState.HIDE;
import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;

public class NewGameActivity extends AppCompatActivity {

    //region Fields

    @BindView(R.id.toolbarNewGame) Toolbar toolbar;
    @BindView(R.id.chipsInputNewGame) ChipsInput chipsInput;
    @BindView(R.id.fabNewGameStartGame) FloatingActionButton fabStartGame;
    private Unbinder unbinder;
    private NewGameViewModel viewModel;
    private String actualChipsImputText;
    private Snackbar snackbar4players;

    //endregion

    //region Lifecycle

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newgame_activity);
        unbinder = ButterKnife.bind(this);
        snackbar4players = Snackbar.make(chipsInput, R.string.just_four_players_please, LENGTH_INDEFINITE);
        setupToolbar();
        setupViewModel();
        observeViewModel();
        setupChips();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_game_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    //endregion

    //region Events

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_create_player:
                showNewPlayerDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showNewPlayerDialog() {
        TextInputLayout til = (TextInputLayout) getLayoutInflater().inflate(
                R.layout.newgame_newplayerdialog_textinputlayout, null);
        TextInputEditText tiet = til.findViewById(R.id.tietNewGameNewPlayerDialog);
        if(StringUtils.isEmpty(actualChipsImputText)) {
            til.setHint(getString(R.string.player_name));
        } else {
            tiet.setText(actualChipsImputText);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.create_player)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    String inputText = tiet.getText().toString().trim();
/*TODO: COMPROBAR QUE NO SE PUEDE AÑADIR UN JUGADOR DOS VECES Y REALIZAR VALIDACIONES SOBRE EL NOMBRE*/
                    viewModel.createPlayer(inputText);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setView(til)
                .create()
                .show();
    }

    @OnClick(R.id.fabNewGameStartGame) void onFabStartGameClick() {
        if(getSelectedPlayerChips().size() != 4) {
            Snackbar.make(chipsInput, getString(R.string.just_four_players), Snackbar.LENGTH_LONG).show();
        } else {
            List<Player> players = obtainPlayers(getSelectedPlayerChips());
            viewModel.createGame(players);
        }
    }

    //endregion

    //region Private

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupViewModel() {
        viewModel = ViewModelProviders
                .of(this, Injector.provideNewGameViewModelFactory(this))
                .get(NewGameViewModel.class);
    }

    private void observeViewModel() {
        viewModel.getAllPlayers().observe(this, this :: setAllPlayers);
        viewModel.getNewPlayer().observe(this, this :: addNewPlayer);
        viewModel.getNewGameId().observe(this, this :: startGame);
        viewModel.getToolbarProgress().observe(this, this :: toggleToolbarProgress);
    }

    private void setAllPlayers(List<Player> allPlayers) {
        chipsInput.setFilterableList(createPlayerChips(allPlayers));
    }

    private void addNewPlayer(Player player) {
        addPlayerChip(player);
        //TODO: ver si en la lista aparece o no o hay que recargarla o que
    }

    private void startGame(long gameId) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(getString(R.string.key_extra_game_id), gameId);
        startActivity(intent);
    }

    private void toogleFabStartGame(ShowState showState) {
        fabStartGame.setVisibility(showState == SHOW ? VISIBLE : GONE);
    }

    private void toggleToolbarProgress(ShowState showState) {
        MenuItem menuItem = toolbar.getMenu().findItem(R.id.action_create_player);
        if(menuItem != null) {
            switch(showState) {
                case HIDE:
                    menuItem.setEnabled(true);
                    menuItem.setActionView(null);
                    break;
                case SHOW:
                    menuItem.setEnabled(false);
                    menuItem.setActionView(R.layout.newgame_toolbar_progress);
                    break;
            }
        }
    }

    //endregion

    //region Chips

    private void setupChips() {
        viewModel.loadAllPlayers();

        chipsInput.addChipsListener(new ChipsInput.ChipsListener() {
            @Override
            public void onChipAdded(ChipInterface chip, int newSize) {
                if(newSize == 4) toogleFabStartGame(SHOW);
                else if(newSize == 5) {
                    toogleFabStartGame(HIDE);
                    snackbar4players.show();
                }
            }

            @Override
            public void onChipRemoved(ChipInterface chip, int newSize) {
                if(newSize == 4) {
                    toogleFabStartGame(SHOW);
                    snackbar4players.dismiss();
                } else if(newSize == 3) toogleFabStartGame(HIDE);
            }

            @Override
            public void onTextChanged(CharSequence text) {
                actualChipsImputText = text.toString();
            }
        });
    }

    private List<PlayerChip> getSelectedPlayerChips() {
        //noinspection unchecked
        return (List<PlayerChip>) chipsInput.getSelectedChipList();
    }

    private List<PlayerChip> createPlayerChips(List<Player> players) {
        List<PlayerChip> playersChips = new ArrayList<>();
        for(Player player : players) {
            playersChips.add(new PlayerChip(player));
        }
        return playersChips;
    }

    private List<Player> obtainPlayers(List<PlayerChip> playersChips) {
        List<Player> players = new ArrayList<>();
        for(PlayerChip playerChip : playersChips) {
            players.add(new Player(playerChip.getPlayer().getPlayerName()));
        }
        return players;
    }

    private void addPlayerChip(Player player) {
        PlayerChip playerChip = new PlayerChip(player);
        chipsInput.addChip(playerChip);
    }

    //endregion
}

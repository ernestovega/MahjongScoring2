package es.etologic.mahjongscoring2.app.new_game;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import es.etologic.mahjongscoring2.app.main.IMainToolbarListener;
import es.etologic.mahjongscoring2.app.model.ShowState;
import es.etologic.mahjongscoring2.app.utils.KeyboardUtils;
import es.etologic.mahjongscoring2.app.utils.StringUtils;
import es.etologic.mahjongscoring2.domain.entities.Player;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static es.etologic.mahjongscoring2.app.model.ShowState.HIDE;
import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;

public class NewGameFragment extends Fragment {

    //region Fields

    @BindView(R.id.toolbarNewGame) Toolbar toolbar;
    @BindView(R.id.chipsInputNewGame) ChipsInput chipsInput;
    @BindView(R.id.fabNewGameCreatePlayer) FloatingActionButton fabCreatePlayer;
    @BindView(R.id.fabNewGameStartGame) FloatingActionButton fabStartGame;
    private Unbinder unbinder;
    private Context context;
    private NewGameViewModel viewModel;
    private IMainToolbarListener mainToolbarListener;
    private List<? extends ChipInterface> lastfilterableList;
    private String actualChipsImputText;
    private Snackbar snackbar4players;

    //endregion

    //region Lifecycle

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.newgame_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        unbinder = ButterKnife.bind(this, view);
        snackbar4players = Snackbar.make(chipsInput, R.string.just_four_players_please,
                Snackbar.LENGTH_INDEFINITE);
        KeyboardUtils.showKeyboard(context, chipsInput.getEditText());
        setupViewModel();
        observeViewModel();
        setupChips();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mainToolbarListener != null && toolbar != null) {
            mainToolbarListener.setToolbar(toolbar);
        }
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    //endregion

    //region Public

    public void setMainToolbarListener(IMainToolbarListener mainToolbarListener) {
        this.mainToolbarListener = mainToolbarListener;
    }

    //endregion

    //region Events

    @OnClick(R.id.fabNewGameCreatePlayer) void onFabCreatePlayerClick() {
        showNewPlayerDialog();
    }

    private void showNewPlayerDialog() {
        TextInputLayout til = (TextInputLayout) getLayoutInflater().inflate(
                R.layout.new_game_new_player_dialog_textinputlayout, null);
        TextInputEditText tiet = til.findViewById(R.id.tietNewGameNewPlayerDialog);
        if(StringUtils.isEmpty(actualChipsImputText)) {
            til.setHint(getString(R.string.player_name));
        } else {
            tiet.setText(actualChipsImputText);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.add_new_player)
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

    private void setupViewModel() {
        viewModel = ViewModelProviders
                .of(this, Injector.provideNewGameViewModelFactory(context))
                .get(NewGameViewModel.class);
    }

    private void observeViewModel() {
        viewModel.getAllPlayers().observe(this, this :: setAllPlayers);
        viewModel.getNewPlayer().observe(this, this :: addNewPlayer);
        viewModel.getNewGameId().observe(this, this :: startGame);
    }

    private void setAllPlayers(List<Player> allPlayers) {
        chipsInput.setFilterableList(createPlayerChips(allPlayers));
    }

    private void addNewPlayer(Player player) {
        addPlayerChip(player);
        //TODO: ver si en la lista aparece o no o hay que recargarla o que
    }

    private void startGame(long gameId) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(getString(R.string.key_extra_game_id), gameId);
        startActivity(intent);
    }

    private void toogleFabStartGame(ShowState showState) {
        fabStartGame.setVisibility(showState == SHOW ? VISIBLE : GONE);
        fabCreatePlayer.setVisibility(showState == SHOW ? GONE : VISIBLE);
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

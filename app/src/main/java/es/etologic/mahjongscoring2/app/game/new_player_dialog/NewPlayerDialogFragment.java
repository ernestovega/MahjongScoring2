package es.etologic.mahjongscoring2.app.game.new_player_dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.ChipInterface;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.app.base.BaseFragment;
import es.etologic.mahjongscoring2.app.main.activity.MainActivityViewModel;
import es.etologic.mahjongscoring2.app.main.activity.MainActivityViewModelFactory;
import es.etologic.mahjongscoring2.app.model.ShowState;
import es.etologic.mahjongscoring2.app.utils.StringUtils;
import es.etologic.mahjongscoring2.domain.model.Player;

import static android.support.design.widget.Snackbar.LENGTH_INDEFINITE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static es.etologic.mahjongscoring2.app.main.activity.MainActivityViewModel.MainScreens.OLD_GAMES;
import static es.etologic.mahjongscoring2.app.model.ShowState.HIDE;
import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;

public class NewPlayerDialogFragment extends BaseFragment {

    //VIEWS
    @BindView(R.id.chipsInputNewGame) ChipsInput chipsInput;
    @BindView(R.id.fabCreatePlayerStartGame) FloatingActionButton fabStartGame;
    //FIELDS
    private Unbinder unbinder;
    @Inject MainActivityViewModelFactory mainActivityViewModelFactory;
    private MainActivityViewModel activityViewModel;
    @Inject NewPlayerDialogViewModelFactory newPlayerDialogViewModelFactory;
    private NewPlayerDialogViewModel viewModel;
    private String actualChipsInputText;
    private Snackbar snackbar4players;

    //EVENTS
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                activityViewModel.navigateTo(OLD_GAMES);
                return true;
            case R.id.action_create_player:
                showNewPlayerDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void showNewPlayerDialog() {
        @SuppressLint("InflateParams")
        TextInputLayout til = (TextInputLayout) getLayoutInflater().inflate(
                R.layout.newgame_newplayerdialog_textinputlayout, null);
        TextInputEditText tiet = til.findViewById(R.id.tietNewGameNewPlayerDialog);
        if(StringUtils.isEmpty(actualChipsInputText)) {
            til.setHint(getString(R.string.player_name));
        } else {
            tiet.setText(actualChipsInputText);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
    @OnClick(R.id.fabCreatePlayerStartGame) void onFabCreatePlayerClick() {
    }
    private List<PlayerChip> getSelectedPlayerChips() {
        //noinspection unchecked
        return (List<PlayerChip>) chipsInput.getSelectedChipList();
    }
    private List<String> obtainPlayersNames(List<PlayerChip> playersChips) {
        List<String> playersNames = new ArrayList<>();
        for(PlayerChip playerChip : playersChips) {
            playersNames.add(playerChip.getPlayer().getPlayerName());
        }
        return playersNames;
    }

    //LIFECYCLE
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.newgame_fragment, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        snackbar4players = Snackbar.make(chipsInput, R.string.just_four_players_please, LENGTH_INDEFINITE);
        setupViewModel();
        setupChips();
        viewModel.bindAllPlayers();
    }
    private void setupViewModel() {
        FragmentActivity activity = getActivity();
        if(activity != null) {
            activityViewModel = ViewModelProviders.of(activity, mainActivityViewModelFactory).get(MainActivityViewModel.class);
            viewModel = ViewModelProviders.of(this, newPlayerDialogViewModelFactory).get(NewPlayerDialogViewModel.class);
            viewModel.getAllPlayers().observe(this, this::setAllPlayers);
            viewModel.getNewPlayer().observe(this, this::addPlayerChip);
            viewModel.getSnackbarMessage().observe(this, this::showSnackbar);
        }
    }
    private void showSnackbar(String message) {
        Snackbar.make(chipsInput, message, Snackbar.LENGTH_LONG).show();
    }
    private void setupChips() {
        chipsInput.addChipsListener(new ChipsInput.ChipsListener() {
            @Override
            public void onChipAdded(ChipInterface chip, int newSize) {
                if(newSize == 4) {
                    toogleFabStartGame(SHOW);
                } else if(newSize == 5) {
                    toogleFabStartGame(HIDE);
                    snackbar4players.show();
                }
            }

            @Override
            public void onChipRemoved(ChipInterface chip, int newSize) {
                if(newSize == 4) {
                    toogleFabStartGame(SHOW);
                    snackbar4players.dismiss();
                } else if(newSize == 3) {
                    toogleFabStartGame(HIDE);
                }
            }

            @Override
            public void onTextChanged(CharSequence text) {
                actualChipsInputText = text.toString();
            }
        });
    }
    private void toogleFabStartGame(ShowState showState) {
        fabStartGame.setVisibility(showState == SHOW ? VISIBLE : GONE);
    }
    private void setAllPlayers(List<Player> allPlayers) {
        chipsInput.setFilterableList(createPlayerChips(allPlayers));
    }
    private List<PlayerChip> createPlayerChips(List<Player> players) {
        List<PlayerChip> playersChips = new ArrayList<>();
        for(Player player : players) {
            if(!isSelected(player)) {
                playersChips.add(new PlayerChip(player));
            }
        }
        return playersChips;
    }
    private boolean isSelected(Player player) {
        List<PlayerChip> selectedPlayerChips = getSelectedPlayerChips();
        if(!selectedPlayerChips.isEmpty()) {
            for (PlayerChip playerChip : selectedPlayerChips) {
                if (playerChip.getPlayer().getPlayerId() == player.getPlayerId()) {
                    return true;
                }
            }
        }
        return false;
    }
    private void addPlayerChip(Player player) {
        PlayerChip playerChip = new PlayerChip(player);
        chipsInput.addChip(playerChip);
        viewModel.bindAllPlayers();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.new_game_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}

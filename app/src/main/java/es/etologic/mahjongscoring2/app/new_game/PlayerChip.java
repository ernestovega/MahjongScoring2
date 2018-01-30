package es.etologic.mahjongscoring2.app.new_game;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.pchmn.materialchips.model.ChipInterface;

import es.etologic.mahjongscoring2.domain.entities.Player;

class PlayerChip implements ChipInterface{

    private Player player;

    public Player getPlayer() {
        return player;
    }

    PlayerChip(Player player) {
        this.player = player;
    }

    @Override
    public String getId() {
        return player.getPlayerName();
    }

    @Override
    public Uri getAvatarUri() {
        return null;
    }

    @Override
    public Drawable getAvatarDrawable() {
        return null;
    }

    @Override
    public String getLabel() {
        return player.getPlayerName();
    }

    @Override
    public String getInfo() {
        return null;
    }
}

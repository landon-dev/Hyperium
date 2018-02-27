/*
 * Hyperium Client, Free client with huds and popular mod
 *     Copyright (C) 2018  Hyperium Dev Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cc.hyperium.mods.togglechat;

import cc.hyperium.event.ChatEvent;
import cc.hyperium.event.InvokeEvent;
import cc.hyperium.event.Priority;
import cc.hyperium.utils.ChatColor;
import cc.hyperium.mods.togglechat.toggles.ToggleBase;
import cc.hyperium.mods.togglechat.toggles.defaults.TypeMessageSeparator;

import net.minecraft.util.ChatComponentText;

/**
 * Handles all the main ToggleChat events for the lightweight
 *      version of ToggleChat!
 *
 * @author boomboompower
 */
public class ToggleChatEvents {

    private ToggleChatMod mod;

    public ToggleChatEvents(ToggleChatMod theMod) {
        this.mod = theMod;
    }

    @InvokeEvent(priority = Priority.HIGH) // We use the high priority to grab things first
    public void onChatReceive(ChatEvent event) {
        // Strip the message of any colors for improved detectability
        String unformattedText = ChatColor.stripColor(event.getChat().getUnformattedText());

        // The formatted message for a few of the custom toggles
        String formattedText = event.getChat().getFormattedText();

        try {
            // Loop through all the toggles
            for (ToggleBase type : this.mod.getToggleHandler().getToggles().values()) {
                // The chat its looking for shouldn't be toggled, move to next one!
                if (type.isEnabled()) {
                    continue;
                }
                
                // We don't want an issue with one toggle bringing
                // the whole toggle system crashing down in flames.
                try {
                    // The text we want to input into the shouldToggle method.
                    String input = type.useFormattedMessage() ? formattedText : unformattedText;

                    // If the toggle should toggle the specified message and
                    // the toggle is not enabled (this message is turned off)
                    // don't send the message to the player & stop looping
                    if (type.shouldToggle(input)) {
                        if (type instanceof TypeMessageSeparator) {
                            event.setChat(new ChatComponentText(((TypeMessageSeparator) type).editMessage(formattedText)));
                        } else {
                            event.setCancelled(true);
                        }
                        break;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
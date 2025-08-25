/*
 * Copyright 2015-2021 Adrien 'Litarvan' Navratil
 *
 * This file is part of OpenAuth.

 * OpenAuth is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenAuth is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenAuth.  If not, see <http://www.gnu.org/licenses/>.
 */
package accounts.altmanager.openauth.microsoft;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.CompletableFuture;

/*
 * Had to use Swing here, JavaFX is meant to have an 'Application' but only one can exist.
 * Creating one would break compatibility with JavaFX apps (which already have their own
 * class), and letting the user do so would break compatibility with Swing apps.
 *
 * This method makes the frame compatible with pretty much everything.
 */

public class LoginFrame extends JFrame
{
    private CompletableFuture<String> future;

    public LoginFrame()
    {
        this.setTitle("Sign in to Minecraft");
        this.setSize(750, 750);
        this.setLocationRelativeTo(null);

    }

    public CompletableFuture<String> start(String url)
    {
        if (this.future != null) {
            return this.future;
        }

        this.future = new CompletableFuture<>();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                future.completeExceptionally(new MicrosoftAuthenticationException("User closed the authentication window"));
            }
        });

        return this.future;
    }

    protected void init(String url)
    {
        try {
            overrideFactory();
        } catch (Throwable ignored) {
            // If the handler was already defined, we can safely ignore this.
            // If it isn't the right one, we can't override it anyway.
        }

        this.setVisible(true);
    }

    protected static void overrideFactory()
    {

    }
}

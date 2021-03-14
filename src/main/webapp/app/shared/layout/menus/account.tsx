import React from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';
import { DropdownItem } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { NavLink as Link } from 'react-router-dom';

import { NavDropdown } from './menu-components';

const accountMenuItemsAuthenticated = (
  <>
    <MenuItem icon="wrench" to="/account/settings">
      Einstellungen
    </MenuItem>
    <MenuItem icon="lock" to="/account/password">
      Passwort ändern
    </MenuItem>
    <MenuItem icon="sign-out-alt" to="/logout">
      Ausloggen
    </MenuItem>
  </>
);

const accountMenuItems = (
  <>
    <MenuItem id="login-item" icon="sign-in-alt" to="/login">
      Einloggen
    </MenuItem>
    <MenuItem icon="sign-in-alt" to="/account/register">
      Registrieren
    </MenuItem>
  </>
);

export const AccountMenu = ({ isAuthenticated = false }) => (
  <NavDropdown icon="user" name="Account" id="account-menu">
    {isAuthenticated ? accountMenuItemsAuthenticated : accountMenuItems}
  </NavDropdown>
);

export default AccountMenu;

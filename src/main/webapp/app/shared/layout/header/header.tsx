import './header.scss';

import React, { useState } from 'react';

import { Navbar, Nav, NavbarToggler, NavbarBrand, Collapse, NavItem, NavLink  } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { NavLink as Link } from 'react-router-dom';
import LoadingBar from 'react-redux-loading-bar';

import { Home, Brand } from './header-components';
import { AdminMenu, EntitiesMenu, AccountMenu } from '../menus';
import {Fragment} from "react";


export interface IHeaderProps {
  isAuthenticated: boolean;
  isAdmin: boolean;
  ribbonEnv: string;
  isInProduction: boolean;
  isSwaggerEnabled: boolean;
}

const Header = (props: IHeaderProps) => {
  const [menuOpen, setMenuOpen] = useState(false);

  const renderDevRibbon = () =>
    props.isInProduction === false ? (
      <div className="ribbon dev">
        <a href="">Development</a>
      </div>
    ) : null;

  const toggleMenu = () => setMenuOpen(!menuOpen);

  const employeeNav = () => {
    return (
      <NavItem>
        <NavLink tag={Link} to="/employee" className="d-flex align-items-center">
          <span>Mitarbeiter</span>
        </NavLink>
      </NavItem>
    )
  }

  const entitiesNav =  () => {
    return (
      <Fragment>
        <NavItem>
          <NavLink tag={Link} to="/vacation-request" className="d-flex align-items-center">
            <span>Urlaubsanträge</span>
          </NavLink>
        </NavItem>
      <NavItem>
        <NavLink tag={Link} to="/sick-leave" className="d-flex align-items-center">
          <span>Krankschreibungen</span>
        </NavLink>
      </NavItem>
      <NavItem>
        <NavLink tag={Link} to="/event" className="d-flex align-items-center">
          <span>Veranstaltungen</span>
        </NavLink>
      </NavItem>
    </Fragment>
    )
  }


  /* jhipster-needle-add-element-to-menu - JHipster will add new menu items here */

  return (
    <div id="app-header">
      {renderDevRibbon()}
      <LoadingBar className="loading-bar" />
      <Navbar dark expand="sm" fixed="top" className="jh-navbar">
        <NavbarToggler aria-label="Menu" onClick={toggleMenu} />
        <Brand />
        <Collapse isOpen={menuOpen} navbar>
          <Nav id="header-tabs" className="ml-auto" navbar>
            {props.isAuthenticated && entitiesNav()}
            {props.isAuthenticated && props.isAdmin && employeeNav()}
            <AccountMenu isAuthenticated={props.isAuthenticated} />
          </Nav>
        </Collapse>
      </Navbar>
    </div>
  );
};

export default Header;

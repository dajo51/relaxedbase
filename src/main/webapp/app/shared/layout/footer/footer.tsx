import './footer.scss';

import React from 'react';

import { Col, Row } from 'reactstrap';

const Footer = props => (
  <div className="footer page-content">
    <Row>
      <Col md="12">
        <p className={"col-sm-4 offset-sm-4 text-center p-2"}>made with ♥ @relaxdays code challenge </p>
      </Col>
    </Row>
  </div>
);

export default Footer;

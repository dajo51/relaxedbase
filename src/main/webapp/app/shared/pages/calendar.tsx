import React, { useEffect, useState } from 'react';

const Calendar = () => {

  return (
    <div className="card col-sm-6 offset-3">
      <div className="card-header justify-center">
        <div className={"row row-content"}>
          <button className={"btn col-sm-1"}><span className={'arrow-left-circle-fill'}/></button>
          <h4 className={"offset-1 col-sm-8 text-center"}>{}</h4>
          <button className={"btn col-sm-1 offset-1"}><span className={'arrow-right-circle-fill'}/></button>
        </div>
      </div>
      <ul className="list-group list-group-flush">
        <li className="list-group-item">
          <div className={"row row-content"}>
            <h4>Montag</h4>
          </div>
          <div className={"row row-content"}>
            <p>Schicht: 10:00 Uhr - 17:00 Uhr</p>
          </div>
        </li>
        <li className="list-group-item">
          <div className={"row row-content"}>
            <h4>Dienstag</h4>
          </div>
          <div className={"row row-content"}>
            <p>Schicht: 10:00 Uhr - 17:00 Uhr</p>
          </div>
        </li>
        <li className="list-group-item">
          <div className={"row row-content"}>
            <h4>Mittwoch</h4>
          </div>
          <div className={"row row-content"}>
            <p>Schicht: 10:00 Uhr - 17:00 Uhr</p>
          </div>
        </li>
        <li className="list-group-item">
          <div className={"row row-content"}>
            <h4>Donnerstag</h4>
          </div>
          <div className={"row row-content"}>
            <p>Schicht: 10:00 Uhr - 17:00 Uhr</p>
          </div>
        </li>
        <li className="list-group-item">
          <div className={"row row-content"}>
            <h4>Freitag</h4>
          </div>
          <div className={"row row-content"}>
            <p>Schicht: 10:00 Uhr - 17:00 Uhr</p>
          </div>
        </li>
        <li className="list-group-item">
          <div className={"row row-content"}>
            <h4>Samstag</h4>
          </div>
          <div className={"row row-content"}>
            <p>Schicht: 10:00 Uhr - 17:00 Uhr</p>
          </div>
        </li>
        <li className="list-group-item">
          <div className={"row row-content"}>
            <h4>Sonntag</h4>
          </div>
          <div className={"row row-content"}>
            <p>Schicht: 10:00 Uhr - 17:00 Uhr</p>
          </div>
        </li>


      </ul>
    </div>
  );
};

export default Calendar;

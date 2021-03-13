import React, { useEffect, useState } from 'react';
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';

const CalendarCard = () => {
  return (
    <div className={"card-columns"}>
      <div className={"card"}>
        <div className={"card-header"}>
          <h3>Kalender</h3>
        </div>
        <div className={"card-body p-0"}>
          <Calendar />
        </div>
      </div>
      <div className={"card"}>
        <div className={"card-header"}>
          <h3>Heute in der Kantine:</h3>
        </div>
        <div className={"card-body p-0"}>
          <ul>
            <li>Schweinebraten mit Kartoffeln</li>
            <li>Spirelli Napoli</li>
            <li>Quarkkeulchen</li>
          </ul>
        </div>
      </div>
    </div>

  );
};

export default CalendarCard;

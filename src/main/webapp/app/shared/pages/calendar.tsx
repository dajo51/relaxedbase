import React, { useEffect, useState } from 'react';

const Calendar = () => {
  const defaultDate = "01.01.2000";

  const [chartStartDate, setChartStartDate] = useState({})
  const [chartEndDate, setChartEndDate] = useState({})

  useEffect(() => {
    getChartStart(locale).then((result) => setChartStartDate(result))
    getChartEnd(locale).then((result) => setChartEndDate(result))
  }, [locale] );

  useEffect(() => {
    if (!isValid(currentlySelectedDate)) {
      setCurrentlySelectedDate(moment(defaultDate))
    }
  }, [chartStartDate, chartEndDate]);

  const [currentlySelectedDate, setCurrentlySelectedDate] = useState(moment(defaultDate, "DD.MM.YYYY"));
  useEffect(() => {
      dateChangedCallback(currentlySelectedDate)
    },
    [currentlySelectedDate]
  );

  const decrementYear = () => {
    setCurrentlySelectedDate((previousDate) => {
      let newDate = previousDate.clone()
      newDate.subtract(1, 'years')
      if (isValid(newDate)) {
        return newDate
      } else {
        return previousDate
      }
    })
  };

  const decrementWeek = () => {
    setCurrentlySelectedDate((previousDate) => {
      let newDate = previousDate.clone()
      newDate.subtract(7, 'days')
      if (isValid(newDate)) {
        return newDate
      } else {
        return previousDate
      }
    })
  };

  const incrementWeek = () => {
    setCurrentlySelectedDate((previousDate) => {
      let newDate = previousDate.clone()
      newDate.add(7, 'days')
      if (isValid(newDate)) {
        return newDate
      } else {
        return previousDate
      }
    })
  };

  const incrementYear = () => {
    setCurrentlySelectedDate((previousDate) => {
      let newDate = previousDate.clone()
      newDate.add(1, 'years')
      if (isValid(newDate)) {
        return newDate
      } else {
        return previousDate
      }
    })
  };
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

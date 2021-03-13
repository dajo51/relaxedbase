import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Col, Row, Table } from 'reactstrap';
import { ICrudGetAllAction, TextFormat, getSortState, IPaginationBaseState, JhiPagination, JhiItemCount } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntities } from './sick-leave.reducer';
import { ISickLeave } from 'app/shared/model/sick-leave.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';

export interface ISickLeaveProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export const SickLeave = (props: ISickLeaveProps) => {
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getSortState(props.location, ITEMS_PER_PAGE), props.location.search)
  );

  const getAllEntities = () => {
    props.getEntities(paginationState.activePage - 1, paginationState.itemsPerPage, `${paginationState.sort},${paginationState.order}`);
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (props.location.search !== endURL) {
      props.history.push(`${props.location.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(props.location.search);
    const page = params.get('page');
    const sort = params.get('sort');
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [props.location.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === 'asc' ? 'desc' : 'asc',
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const { sickLeaveList, match, loading, totalItems } = props;
  return (
        <div className={"col-sm-8 offset-sm-2"}>
          <h2 id="vacation-request-heading">
            Krankschreibungen
          </h2>
          <div className={"card-columns"}>
            {sickLeaveList.map((sickLeave, i) => (
              <div className="card p-0" key={`entity-${i}`}>
                <div className={"card-header d-flex justify-content-between align-items-center mb-3"}>
                  <h5 className="card-title mb-0">{sickLeave.startDate ? <TextFormat type="date" value={sickLeave.startDate} format={APP_LOCAL_DATE_FORMAT} /> : null} -
                    {sickLeave.endDate ? <TextFormat type="date" value={sickLeave.endDate} format={APP_LOCAL_DATE_FORMAT} /> : null}
                  </h5>
                  <Button
                    tag={Link}
                    to={`${match.url}/${sickLeave.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                    color={"danger"}
                    size={"sm"}
                  ><FontAwesomeIcon icon="trash" />
                  </Button>
                </div>
                <div className="card-body">
                  <p className="card-text">Angestellter: {sickLeave.employee ? <Link to={`employee/${sickLeave.employee.id}`}>{sickLeave.employee.firstName}{sickLeave.employee.lastName}</Link> : ''}</p>
                </div>
              </div>
            ))}
          </div>
          <div id="addButton">
            <Link to={`${match.url}/new`} className="btn btn-primary rounded-circle btn-xl">
              <FontAwesomeIcon icon="plus" />
            </Link>
          </div>
          {props.totalItems ? (
            <div className={sickLeaveList && sickLeaveList.length > 0 ? '' : 'd-none'}>
              <Row className="justify-content-center">
                <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} />
              </Row>
              <Row className="justify-content-center">
                <JhiPagination
                  activePage={paginationState.activePage}
                  onSelect={handlePagination}
                  maxButtons={5}
                  itemsPerPage={paginationState.itemsPerPage}
                  totalItems={props.totalItems}
                />
              </Row>
            </div>
          ) : (
            ''
          )}
        </div>
        );
        };



const mapStateToProps = ({ sickLeave }: IRootState) => ({
  sickLeaveList: sickLeave.entities,
  loading: sickLeave.loading,
  totalItems: sickLeave.totalItems,
});

const mapDispatchToProps = {
  getEntities,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(SickLeave);

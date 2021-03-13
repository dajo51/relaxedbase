import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router-dom';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';
import { ICrudGetAction, ICrudDeleteAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IVacationRequest } from 'app/shared/model/vacation-request.model';
import { IRootState } from 'app/shared/reducers';
import { getEntity, deleteEntity } from './vacation-request.reducer';

export interface IVacationRequestDeleteDialogProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const VacationRequestDeleteDialog = (props: IVacationRequestDeleteDialogProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const handleClose = () => {
    props.history.push('/vacation-request' + props.location.search);
  };

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const confirmDelete = () => {
    props.deleteEntity(props.vacationRequestEntity.id);
  };

  const { vacationRequestEntity } = props;
  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose}>Bestätigung</ModalHeader>
      <ModalBody id="relaxedbaseApp.vacationRequest.delete.question">Bist du dir sicher, dass du deinen Urlaubsantrag löschen möchtest?</ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp; Abbrechen
        </Button>
        <Button id="jhi-confirm-delete-vacationRequest" color="danger" onClick={confirmDelete}>
          <FontAwesomeIcon icon="trash" />
          &nbsp; Löschen
        </Button>
      </ModalFooter>
    </Modal>
  );
};

const mapStateToProps = ({ vacationRequest }: IRootState) => ({
  vacationRequestEntity: vacationRequest.entity,
  updateSuccess: vacationRequest.updateSuccess,
});

const mapDispatchToProps = { getEntity, deleteEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(VacationRequestDeleteDialog);

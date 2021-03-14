import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router-dom';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';
import { ICrudGetAction, ICrudDeleteAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { ISickLeave } from 'app/shared/model/sick-leave.model';
import { IRootState } from 'app/shared/reducers';
import { getEntity, deleteEntity } from './sick-leave.reducer';

export interface ISickLeaveDeleteDialogProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const SickLeaveDeleteDialog = (props: ISickLeaveDeleteDialogProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const handleClose = () => {
    props.history.push('/sick-leave' + props.location.search);
  };

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const confirmDelete = () => {
    props.deleteEntity(props.sickLeaveEntity.id);
  };

  const { sickLeaveEntity } = props;
  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose}>Bestätigung</ModalHeader>
      <ModalBody id="relaxedbaseApp.sickLeave.delete.question">Bist du dir sicher, dass du deine Krankschreibung löschen möchtest?</ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp; Abbrechen
        </Button>
        <Button id="jhi-confirm-delete-sickLeave" color="danger" onClick={confirmDelete}>
          <FontAwesomeIcon icon="trash" />
          &nbsp; Löschen
        </Button>
      </ModalFooter>
    </Modal>
  );
};

const mapStateToProps = ({ sickLeave }: IRootState) => ({
  sickLeaveEntity: sickLeave.entity,
  updateSuccess: sickLeave.updateSuccess,
});

const mapDispatchToProps = { getEntity, deleteEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(SickLeaveDeleteDialog);

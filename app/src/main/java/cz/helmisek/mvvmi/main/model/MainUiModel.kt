package cz.helmisek.mvvmi.main.model

import cz.helmisek.mvvmilib.BaseModel
import cz.helmisek.mvvmi.entity.Person
import cz.helmisek.mvvmi.entity.personAge
import cz.helmisek.mvvmi.entity.personFirstName
import cz.helmisek.mvvmi.entity.personLastName
import kategory.lenses
import kategory.optics.modify

@lenses data class MainUiModel(val person: Person = Person()) : BaseModel()

fun MainUiModel.modifyFirstName(f: (String) -> String): MainUiModel {
    return (mainUiModelPerson() compose personFirstName()).modify(this, f)
}

fun MainUiModel.modifyLastName(f: (String) -> String): MainUiModel {
    return (mainUiModelPerson() compose personLastName()).modify(this, f)
}

fun MainUiModel.modifyAge(f: (String) -> String): MainUiModel {
    return (mainUiModelPerson() compose personAge()).modify(this, f)
}
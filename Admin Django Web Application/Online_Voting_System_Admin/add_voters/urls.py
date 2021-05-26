from django.urls import path
from .views import *




urlpatterns = [
    path("dob_validator",dob_validator,name="dob_validator"),
    path("face_detector",face_detector,name="face_detector"),
    path("ajax_voter_add",ajax_voter_add,name="ajax_voter_add"),
    path("state_submission",state_submission,name='state_submission'),
    path("district_submission",district_submission,name="district_submission")
]
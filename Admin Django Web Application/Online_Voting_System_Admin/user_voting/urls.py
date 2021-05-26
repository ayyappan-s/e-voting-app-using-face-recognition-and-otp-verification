from django.urls import path
from .views import *



urlpatterns = [
        path("check_user",check_user,name="check_user"),
        path("face_recognizer",face_recognizer,name="face_recognizer"),
        path("addvote",addvote,name="addvote"),
        path("voter_confirmation",voter_confirmation,name="voter_confirmation"),
]

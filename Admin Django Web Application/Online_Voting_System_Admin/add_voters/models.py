from django.db import models

class voters_model(models.Model):
    name = models.CharField(max_length=100)
    phone = models.CharField(max_length=20)
    area = models.CharField(max_length=30)
    image = models.CharField(max_length=200)
    dob = models.DateField(max_length=11)

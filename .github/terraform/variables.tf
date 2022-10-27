
variable "svc_display_name" {
  type    = string
  default = "endless-api"
}

variable "svc_port" {
  type    = number
  default = 8080
}

variable "region" {
  type    = string
  default = "us-west-2"
}

variable "ecr_repo" {
  type    = string
  default = "bradlet"
}

variable "vpc_cidr" {
  type    = string
  default = "20.0.0.0/16"
}

variable "vpc_name" {
  type    = string
  default = "default"
}

variable "alb_name" {
  type    = string
  default = "endless-lb"
}

variable "group_name" {
  type    = string
  default = "endless"
}
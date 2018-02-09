/*
 *
 * Project Name:     Neural Network based Motorcycle Ecosystem
 * Author List:      Rohan Mahajan, Kartikeya Kawadkar
 * Filename:         Accelerometer.c
 * Function:         void app_main()
 * Global Variables: i2c_config_t conf, uint8_t data[6], short accel_x, short accel_y, short accel_z
 *
 */







#include<stdio.h>
#include "esp_system.h"
#include "driver/i2c.h"
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "driver/gpio.h"

i2c_config_t conf;

uint8_t data[6];

short accel_x;
short accel_y;
short accel_z;

void app_main()
{
  //Configuring the I2C Communication in ESP32
  //SDA = pin 33 , SCL = pin 32 , Serial Clock frequency = 400kHz
  conf.mode = I2C_MODE_MASTER;
  conf.sda_io_num = GPIO_NUM_33;
  conf.scl_io_num = GPIO_NUM_32;
  conf.sda_pullup_en = GPIO_PULLUP_ENABLE;
  conf.scl_pullup_en = GPIO_PULLUP_ENABLE;
  conf.master.clk_speed = 400000;
  i2c_param_config(I2C_NUM_0, &conf);
  i2c_driver_install(I2C_NUM_0, I2C_MODE_MASTER, 0, 0, 0);

  i2c_cmd_handle_t cmd;
  
  //Sending command to MPU6050 to change its power mode to normal
  cmd = i2c_cmd_link_create();
  i2c_master_start(cmd);
  i2c_master_write_byte(cmd, (0x68 << 1) | I2C_MASTER_WRITE, 1);
  i2c_master_write_byte(cmd, 0x6B, 1);
  i2c_master_stop(cmd);
  i2c_master_cmd_begin(I2C_NUM_0, cmd, 1000/portTICK_PERIOD_MS);
  i2c_cmd_link_delete(cmd);

  cmd = i2c_cmd_link_create();
  i2c_master_start(cmd);
  i2c_master_write_byte(cmd, (0x68 << 1) | I2C_MASTER_READ, 1);
  i2c_master_read_byte(cmd, data, 1);
  i2c_master_stop(cmd);
  i2c_master_cmd_begin(I2C_NUM_0, cmd, 1000/portTICK_PERIOD_MS);
  i2c_cmd_link_delete(cmd);

  cmd = i2c_cmd_link_create();
  i2c_master_start(cmd);
  i2c_master_write_byte(cmd, (0x68 << 1) | I2C_MASTER_WRITE, 1);
  i2c_master_write_byte(cmd, 0x6B, 1);
  i2c_master_write_byte(cmd, 0x00, 1);
  i2c_master_stop(cmd);
  i2c_master_cmd_begin(I2C_NUM_0, cmd, 1000/portTICK_PERIOD_MS);
  i2c_cmd_link_delete(cmd);

  // Reading Acceleration in X, Y and Z directions and logging through Serial
  while(1)
  {
        ets_delay_us(99000);

        cmd = i2c_cmd_link_create();
  		i2c_master_start(cmd);
  		i2c_master_write_byte(cmd, (0x68 << 1) | I2C_MASTER_WRITE, 1);
  		i2c_master_write_byte(cmd, 0x3B, 1);
  		i2c_master_stop(cmd);
  		i2c_master_cmd_begin(I2C_NUM_0, cmd, 1000/portTICK_PERIOD_MS);
  		i2c_cmd_link_delete(cmd);

  		cmd = i2c_cmd_link_create();
  		i2c_master_start(cmd);
  		i2c_master_write_byte(cmd, (0x68 << 1) | I2C_MASTER_READ, 1);

  		i2c_master_read_byte(cmd, data,   0);
  		i2c_master_read_byte(cmd, data+1, 0);
  		i2c_master_read_byte(cmd, data+2, 0);
  		i2c_master_read_byte(cmd, data+3, 0);
  		i2c_master_read_byte(cmd, data+4, 0);
  		i2c_master_read_byte(cmd, data+5, 1);

  		i2c_master_stop(cmd);
  		i2c_master_cmd_begin(I2C_NUM_0, cmd, 1000/portTICK_PERIOD_MS);
  		i2c_cmd_link_delete(cmd);

        accel_x = (data[0] << 8) | data[1];
  		accel_y = (data[2] << 8) | data[3];
  		accel_z = (data[4] << 8) | data[5];

  		printf("%d,%d,%d\n", accel_x, accel_y, accel_z);


  }




  }
